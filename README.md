# corfield.org source code

This is the repository for [corfield.org](https://corfield.org).
The **cryogen** branch is the source version.
The **master** branch is the generated HTML version.

The **source** branch is the previous Octopress/Jekyll-powered version.
See the [migration code](https://github.com/seancorfield/seancorfield.github.io/blob/cryogen/src/migrate_blog.clj) I wrote to convert the `yyyy-MM-dd-title.markdown` files for Jekyll to the `yyyy/MM/dd/title.md` files for Cryogen, including reformatting the Jekyll prelude in each file as an EDN hash map for Cryogen.

## Why Cryogen?

Why did I migrate? The Ruby ecosystem has always seemed pretty fragile on my old Mac (10.12) and Dependabot has flagged a lot of security vulnerabilities in various gems -- but reliably updating components and Ruby has increasingly become a very frustrating and fraught process. I was using `rbenv` and Ruby 2.2.3 successfully for quite a while but that just stopped working (without even touching any of the code or setup) and after wrestling with it all for a weekend, I just gave in and decided to switch to [Cryogen](https://cryogenweb.org/) after seeing other Clojurians recommend it. So far it seems nice and simple and reliable.

## Content Warning

The main corfield.org site is my technical blog. The site also contains an archive of [The Corfield Story](https://corfield.org/camera/) by Bev Parker, documenting my family's camera business, from 1950's England, run by my father and his brother (both deceased now). The original "local history" website version is no longer hosted by [The University of Wolverhampton](https://www.wlv.ac.uk/).

In addition, the site contains some bodyart pages that are still linked from various bodyart websites around the world. That section is not linked from the main corfield.org site, but it is present in the `content` folder here (and on the generated content branch). As it says on the index page for the bodyart section:

> These pages contain information
about and images of body piercing and tattooing. Some of the material is
sexually explicit and may offend.
