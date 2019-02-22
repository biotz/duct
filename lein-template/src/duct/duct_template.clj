(ns duct.duct-template
  (:require [clojure.java.io :as io]))

(defn resource [path]
  (io/resource (str "duct/duct_template/" path)))

(def ^:private web-directories
  ["resources/{{dirs}}/public"
   "src/{{dirs}}/handler"
   "test/{{dirs}}/handler"])

(defn example-profile [{:keys [profiles]}]
  {:vars {:example? true}
   :templates
   (cond
     (profiles :site)
     {"src/{{dirs}}/handler/example.clj"       (resource "example/handler.clj")
      "test/{{dirs}}/handler/example_test.clj" (resource "example/handler_test.clj")
      "resources/{{dirs}}/handler/example/example.html" (resource "example/example.html")}

     (profiles :api)
     {"src/{{dirs}}/handler/example.clj"       (resource "example/handler.clj")
      "test/{{dirs}}/handler/example_test.clj" (resource "example/handler_test.clj")}

     :else
     {"src/{{dirs}}/service/example.clj"       (resource "example/service.clj")
      "test/{{dirs}}/service/example_test.clj" (resource "example/service_test.clj")})})

(defn api-profile [_]
  {:deps '[[duct/module.web "0.7.0"]]
   :dev-deps '[[kerodon "0.9.0"]]
   :vars {:api? true}
   :extra-modules {:duct.module.web/api {}}
   :dirs web-directories})

(defn site-profile [_]
  {:deps     '[[duct/module.web "0.7.0"]]
   :dev-deps '[[kerodon "0.9.0"]]
   :vars     {:site? true}
   :extra-modules {:duct.module.web/site {}}
   :dirs     web-directories})

(defn cljs-profile [{:keys [project-ns] :as foo}]
  {:deps '[[duct/module.web "0.7.0"]
           [duct/module.cljs "0.4.0"]]
   :dev-deps '[[kerodon "0.9.0"]]
   :vars {:cljs? true}
   :dirs web-directories
   :extra-modules {:duct.module/cljs {:main (symbol (str project-ns ".client"))}}
   :templates {"src/{{dirs}}/client.cljs" (resource "cljs/client.cljs")}})

(defn heroku-profile [{:keys [project-name]}]
  {:vars      {:uberjar-name (str project-name "-standalone.jar")}
   :templates {"Procfile" (resource "heroku/Procfile")}})

(defn postgres-profile [_]
  {:deps '[[duct/module.sql "0.5.0"]
           [org.postgresql/postgresql "42.2.5"]]
   :extra-modules {:duct.module/sql {}}
   :vars {:jdbc?        true
          :postgres?    true
          :dev-database "jdbc:postgresql://localhost/postgres"}})

(defn sqlite-profile [_]
  {:deps '[[duct/module.sql "0.5.0"]
           [org.xerial/sqlite-jdbc "3.25.2"]]
   :dirs ["db"]
   :extra-modules {:duct.module/sql {}}
   :vars {:jdbc?        true
          :sqlite?      true
          :dev-database "jdbc:sqlite:db/dev.sqlite"}})

(defn ataraxy-profile [_]
  {:deps '[[duct/module.ataraxy "0.3.0"]]
   :vars {:ataraxy? true, :web? true}
   :extra-base-profile-config {}
   :dirs web-directories})
