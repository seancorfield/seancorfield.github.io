{:title "Calva, Joyride, and Portal",
 :date "2022-12-09 10:00:00",
 :tags ["clojure" "calva" "joyride" "portal" "vs code"]}

https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva

https://marketplace.visualstudio.com/items?itemName=djblue.portal

https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.joyride

`ws.dev-nrepl-portal.impl`:

```clojure
(defn maybe-start-server
  "Given a symbol identifying a function to start a server,
  derive a property name for the port to use and see if we
  can start the server."
  [start-server-fn]
  (let [[server-type] (str/split (namespace start-server-fn) #"\.")
        property      (str server-type ".server.port")]
    (when-let [port-str (System/getProperty property)]
      (try
        (let [port         (parse-long port-str)
              start-server (requiring-resolve start-server-fn)]
          (start-server {:port port})
          (logger/info server-type "server started on port" port))
        (catch Throwable t
          (logger/error property
                        "property exists, but either"
                        "the port is invalid,"
                        "the library is not available,"
                        "or the server cannot be started:"
                        (type t) (ex-message t)))))))
```

`ws.dev-nrepl-portal.interface`:

```clojure
(defonce ^{:doc "Optional nREPL server instance."} nrepl-server
  (impl/maybe-start-server 'nrepl.server/start-server))

(defonce ^{:doc "Optional Portal server instance."} portal-server
  (impl/maybe-start-server 'portal.api/start))
```

```clojure
;; scratch pad for debugging against Admin on QA/production

(ns sean-repl-scratch
  (:require [next.jdbc :as jdbc]
            [portal.api :as p]
            [ws.admin-web.interface.system :refer [sys]]))

(set! *warn-on-reflection* true)

(defn- as-table [v] (with-meta v {:portal.viewer/default :portal.viewer/table}))

(comment

  ;; set the theme:
  (p/eval-str (str
               '(portal.ui.state/dispatch! portal.ui.state/state
                                           portal.ui.state/set-theme!
                                           :portal.colors/zerodark)))

  ;; start tapping...
  (add-tap #'p/submit)

  ;; don't forget this:
  (p/clear)
  (remove-tap #'p/submit)

  ;; a useful datasource:

  (def dsr (-> sys :application :datasource))

  ;; =================== scratch code for debugging ===================

  (as-table (jdbc/execute! dsr ["describe table"]))

  (as-table
   (jdbc/execute! dsr [(str "select * from table"
                            " where name like 'email%'")]))
  )
```

```clojure
    {
      "key": "ctrl+alt+b q",
      "command": "joyride.runUserScript",
      "args": "qa_repl.cljs"
    },
```

```bash
ssh -N \
  -L ${nrepl_port}:localhost:${nrepl_port} \
  -L ${portal_port}:localhost:${portal_port} \
  ${user}@${full_ip}
```

```clojure
(ns qa-repl
  (:require ["vscode" :as vscode]
            [promesa.core :as p]))

(defn- start-qa-tunnel []
  (let [terminal (vscode/window.createTerminal #js {:isTransient true
                                                    :name "QA"
                                                    :message "QA Remote REPL..."})]
    (.show terminal)
    (.sendText terminal "build/bin/tunnel.sh qa")))

(defn- start-qa-browser []
  (vscode/commands.executeCommand "simpleBrowser.show" "http://localhost:<portal_port>")
  (p/do
    (p/delay 2000)
    (vscode/commands.executeCommand "workbench.action.moveEditorToRightGroup")
    (p/delay 1000)
    (vscode/commands.executeCommand "workbench.action.focusFirstEditorGroup")))

(defn- connect-qa-repl []
  (vscode/commands.executeCommand "calva.disconnect")
  (vscode/commands.executeCommand "calva.connect"))

(defn- qa-setup []
  (start-qa-tunnel)
  (p/do
    (p/delay 2000)
    (start-qa-browser)
    (p/delay 1000)
    (connect-qa-repl)))

(qa-setup)
```

```json
    "calva.replConnectSequences": [
      {
        "name": "QA nREPL Server",
        "projectType": "deps.edn",
        "cljsType": "none",
        "nReplPortFile": ["development", "resources", "qa.nrepl"],
        "menuSelections": {}
      }
    ],
```
