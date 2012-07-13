# geotiff

A Clojure library designed to extract tags from GeoTIFF images.

## Usage

###Get spatial extent of a geotiff
(require '[geotiff.core :as geo])

(def reader (geo/get-reader filepath))
(def metadata (geo/get-metadata reader))
(def root (geo/get-root-node metadata))

(defn get-extent []
  (let [[_ _ _ x1 y1 _] (geo/get-model-tie-points root)
        [scale-x scale-y _] (geo/get-model-pixel-scales root)
        [width height] (geo/get-dimensions reader)]
    {:top y1 :left x1 :bottom (- y1 (* height scale-y)) :right (+ x1 (* width scale-x))}))

###Get projected coordinate system type
(geo/get-geokey geo/ProjectedCSTypeGeoKey root)


## License

Copyright © 2012 Jeffrey Burnett

Distributed under the Eclipse Public License, the same as Clojure.
