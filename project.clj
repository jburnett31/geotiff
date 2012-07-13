(defproject geotiff "0.1.1"
  :description "Clojure library for extracting information from GeoTIFF images"
  :url "https://github.com/jburnett31/geotiff"
  :repositories {"mygrid" "http://www.mygrid.org.uk/maven/repository"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [net.java.dev.jai-imageio/jai-imageio-core-standalone "1.2-pre-dr-b04-2011-07-04"]
				 [trammel "0.7.0"]])
