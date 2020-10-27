(ns migrate-blog
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]))

(def ^:private source "/Developers/workspace/seancorfield-source/source/_posts")
(def ^:private target "/Developers/workspace/seancorfield.github.io/content/md/posts")

(defn get-posts
  []
  (->> source (io/file) (.list)
       (map (fn [f] (re-find #"(\d+)-(\d\d)-(\d\d)-(.*)\.m(ark)d(own)" f)))
       (map (fn [[f y m d slug]]
              {:file f :year y :month m :day d :slug slug}))))

(defn read-post
  [f]
  (with-open [rdr (io/reader (str source "/" f))]
    (loop [lines (line-seq rdr) prelude {}]
      (let [line (first lines)]
        (cond (and (str/starts-with? line "---") (empty? prelude))
              (recur (rest lines) prelude)
              (str/starts-with? line "---")
              {:prelude prelude :body (str/join "\n" (rest lines))}
              :else
              (let [[_ k v] (re-find #"([a-z]+): (.*)" line)
                    k (keyword k)
                    prelude'
                    (cond (= :date k)
                          (assoc prelude k v)
                          (= :title k)
                          (assoc prelude k (edn/read-string v))
                          (= :published k)
                          (do
                            (tap> [f 'has-published-flag])
                            (assoc prelude :draft? (not (edn/read-string v))))
                          (= :categories k)
                          (let [tags (edn/read-string (str/trim v))]
                            (assoc prelude
                                   :tags (mapv name
                                               (if (vector? tags)
                                                 tags
                                                 [tags]))))
                          :else
                          prelude)]
                (recur (rest lines) prelude')))))))

(defn process-file
  [{:keys [file year month day slug]}]
  (let [{:keys [prelude body]} (read-post file)
        output (io/file (str target "/" year "/" month "/" day "/" slug ".md"))]
    (io/make-parents output)
    (spit output (with-out-str
                   (pp/pprint prelude)
                   (println body)))))

(comment
  (get-posts)
  (read-post (:file (first (get-posts))))
  (process-file (first (get-posts)))
  (run! process-file (get-posts))
  ,)
