(ns geotiff.core
  (:require [trammel.provide :as provide])
  (:import [java.io File]
           [com.sun.media.imageio.plugins.tiff GeoTIFFTagSet TIFFDirectory TIFFField TIFFTagSet]
           [org.w3c.dom Node NodeList]
           [javax.imageio ImageIO ImageReader]
           [javax.imageio.metadata IIOMetadata IIOMetadataNode]))

(def Angular_Arc_Minute 9103)
(def Angular_Arc_Second 9104)
(def Angular_Degree 9102)
(def Angular_DMS 9107)
(def Angular_DMS_Hemisphere 9108)
(def Angular_Gon 9106)
(def Angular_Grad 9105)
(def Angular_Radian 9101)
(def GCS_NAD27 4267)
(def GCS_NAD83 4269)
(def GCS_WGS_72 4322)
(def GCS_WGS_72BE 4324)
(def GCS_WGS_84 4326)
(def GCSE_WGS84 4030)
(def GEO_KEY_DIRECTORY_VERSION_INDEX 0)
(def GEO_KEY_MINOR_REVISION_INDEX 2)
(def GEO_KEY_NUM_KEYS_INDEX	3)
(def GEO_KEY_REVISION_INDEX	1)
(def GeogAngularUnitsGeoKey	2054)
(def GeogAngularUnitSizeGeoKey 2055)
(def GeogAzimuthUnitsGeoKey	2060)
(def GeogCitationGeoKey 2049)
(def GeogEllipsoidGeoKey 2056)
(def GeogGeodeticDatumGeoKey 2050)
(def GeogInvFlatteningGeoKey 2059)
(def GeogLinearUnitsGeoKey 2052)
(def GeogLinearUnitSizeGeoKey 2053)
(def GeogPrimeMeridianGeoKey 2051)
(def GeogPrimeMeridianLongGeoKey 2061)
(def GeographicTypeGeoKey 2048)
(def GeogSemiMajorAxisGeoKey 2057)
(def GeogSemiMinorAxisGeoKey 2058)
(def GTCitationGeoKey 1026)
(def GTModelTypeGeoKey 1024)
(def GTRasterTypeGeoKey 1025)
(def Linear_Chain_Benoit 9010)
(def Linear_Chain_Sears 9011)
(def Linear_Fathom 9014)
(def Linear_Foot 9002)
(def Linear_Foot_Clarke 9005)
(def Linear_Foot_Indian 9006)
(def Linear_Foot_Modified_American 9004)
(def Linear_Foot_US_Survey 9003)
(def Linear_Link 9007)
(def Linear_Link_Benoit 9008)
(def Linear_Link_Sears 9009)
(def Linear_Meter 9001)
(def Linear_Mile_International_Nautical 9015)
(def Linear_Yard_Indian 9013)
(def Linear_Yard_Sears 9012)
(def ModelTypeGeocentric 3)
(def ModelTypeGeographic 2)
(def ModelTypeProjected 1)
(def NUMBER_ATTR "number")
(def PCS_WGS72_UTM_zone_1N 32201)
(def PCS_WGS72_UTM_zone_1S 32301)
(def PCS_WGS72_UTM_zone_60N 32260)
(def PCS_WGS72_UTM_zone_60S 32360)
(def PCS_WGS72BE_UTM_zone_1N 32401)
(def PCS_WGS72BE_UTM_zone_1S 32501)
(def PCS_WGS72BE_UTM_zone_60N 32460)
(def PCS_WGS72BE_UTM_zone_60S 32560)
(def PCS_WGS84_UTM_zone_1N 32601)
(def PCS_WGS84_UTM_zone_1S 32701)
(def PCS_WGS84_UTM_zone_60N 32660)
(def PCS_WGS84_UTM_zone_60S 32760)
(def PCSCitationGeoKey 3073)
(def ProjAzimuthAngleGeoKey 3094)
(def ProjCenterEastingGeoKey 3090)
(def ProjCenterLatGeoKey 3089)
(def ProjCenterLongGeoKey 3088)
(def ProjCenterNorthingGeoKey 3091)
(def ProjCoordTransGeoKey 3075)
(def ProjectedCSTypeGeoKey 3072)
(def ProjectionGeoKey 3074)
(def ProjFalseEastingGeoKey 3082)
(def ProjFalseNorthingGeoKey 3083)
(def ProjFalseOriginEastingGeoKey 3086)
(def ProjFalseOriginLatGeoKey 3085)
(def ProjFalseOriginLongGeoKey 3084)
(def ProjFalseOriginNorthingGeoKey 3087)
(def ProjLinearUnitsGeoKey 3076)
(def ProjLinearUnitSizeGeoKey 3077)
(def ProjNatOriginLatGeoKey 3081)
(def ProjNatOriginLongGeoKey 3080)
(def ProjScaleAtCenterGeoKey 3093)
(def ProjScaleAtNatOriginGeoKey 3092)
(def ProjStdParallel1GeoKey 3078)
(def ProjStdParallel2GeoKey 3079)
(def ProjStraightVertPoleLongGeoKey 3095)
(def RasterPixelIsArea 1)
(def RasterPixelIsPoint 2)
(def TIFF_ASCII_TAG "TIFFAscii")
(def TIFF_ASCIIS_TAG "TIFFAsciis")
(def TIFF_DOUBLE_TAG "TIFFDouble")
(def TIFF_DOUBLES_TAG "TIFFDoubles")
(def TIFF_FIELD_TAG "TIFFField")
(def TIFF_IFD_TAG "TIFFIFD")
(def TIFF_RATIONAL_TAG "TIFFRational")
(def TIFF_RATIONALS_TAG "TIFFRationals")
(def TIFF_SHORT_TAG "TIFFShort")
(def TIFF_SHORTS_TAG "TIFFShorts")
(def VALUE_ATTR "value")
(def VerticalCitationGeoKey 4097)
(def VerticalCSTypeGeoKey 4096)
(def VerticalDatumGeoKey 4098)
(def VerticalUnitsGeoKey 4099)

(defn get-reader [filepath]
  (let [file (File. filepath)
        iis (ImageIO/createImageInputStream file)
        readers (ImageIO/getImageReadersByFormatName "tif")
        reader (.next readers)]
    (.setInput reader iis)
    reader))

(defn get-metadata [reader]
  (try (.getImageMetadata reader 0)
       (catch IllegalStateException e
         (binding [*out* *err*]
         (println (.getMessage e))))))

(defprotocol Node-List-Seq
  "A protocol to turn DOM node lists into clojure sequences"
  (node-list-seq [this] "convert to seq"))

(extend-type org.w3c.dom.NodeList
  Node-List-Seq (node-list-seq [this]
                  (map (fn [^Integer index] (.item this index))
                       (range (.getLength this)))))

(extend-type org.w3c.dom.NamedNodeMap
  Node-List-Seq (node-list-seq [this]
                  (map (fn [^Integer index] (.item this index))
                       (range (.getLength this)))))

(extend-type nil
  Node-List-Seq (node-list-seq [this] '()))

(defn get-root-node
  "Takes the image metadata and returns the root dom node"
  [metadata]
  (let [formatName (.getNativeMetadataFormatName metadata)]
           (.getAsTree metadata formatName)))

(defn get-tiff-directory
  "Returns the TIFFDirectory of the root node"
  [^Node root]
  (.getFirstChild root))

(defn- build-map [nodes]
  (letfn [(idx [^Node x] (read-string (.. x (getAttributes) (getNamedItem NUMBER_ATTR) (getNodeValue))))]
    (loop [pairs {}
           dataz nodes]
      (if (empty? dataz)
        pairs
        (recur (assoc pairs (idx (first dataz)) (first dataz)) (next dataz))))))

(defn get-tiff-field
  "Get the TIFFField specified by the integer tag from the root node"
  [tag root]
  (let [tiff-directory (get-tiff-directory root)
        children (node-list-seq (.getElementsByTagName tiff-directory TIFF_FIELD_TAG))
        pairs (build-map children)]
    (pairs tag)
    ))

(defn get-value-attribute
  "Returns the node's value attributes as string"
  [node]
  (.. node (getAttributes) (getNamedItem VALUE_ATTR) (getNodeValue)))

(defn get-int-value-attribute [node]
  (int (read-string (get-value-attribute node))))

(defn get-rational-value-attribute [node]
  (rationalize (read-string (get-value-attribute node))))

(defn get-tiff-shorts [tiffField]
  (let [elem (.getFirstChild tiffField)
        shorts (node-list-seq (.getElementsByTagName elem TIFF_SHORT_TAG))]
    (map get-int-value-attribute shorts)))

(defn get-tiff-short [tiffField index]
  (let [elem (.getFirstChild tiffField)
        shorts (.getElementsByTagName elem TIFF_SHORT_TAG)]
    (get-int-value-attribute (.item shorts index))))

(defn get-tiff-doubles [tiffField]
  (if (not (nil? tiffField))
    (let [elem (.getFirstChild tiffField)
          doubles (node-list-seq (.getElementsByTagName elem TIFF_DOUBLE_TAG))]
      (if (not (empty? doubles))
        (map #(double (read-string (get-value-attribute %))) doubles)))))

(defn get-tiff-double [tiffField ^Integer index]
  (let [elem (.getFirstChild tiffField)
        doubles (.getElementsByTagName elem TIFF_DOUBLE_TAG)]
    (double (read-string (get-value-attribute (.item doubles index))))))

(defn get-tiff-rationals [tiffField]
  (let [elem (.getFirstChild tiffField)
        nums (node-list-seq (.getElementsByTagName elem TIFF_RATIONAL_TAG))]
    (map get-rational-value-attribute nums)))

(defn get-tiff-rational [tiffField index]
  (let [elem (.getFirstChild tiffField)
        nums (.getElementsByTagName elem TIFF_RATIONAL_TAG)]
    (get-rational-value-attribute (.item nums index))))

(defn get-tiff-ascii [tiffField start length]
  (let [elem (.getFirstChild tiffField)
        asciis (.getElementsByTagName elem TIFF_ASCII_TAG)
        node (.item asciis 0)]
    (apply str (butlast (get-value-attribute node)))))

(defn get-model-transformation [root]
  (let [node (get-tiff-field GeoTIFFTagSet/TAG_MODEL_TRANSFORMATION root)]
    (get-tiff-doubles node)))

(defn get-model-tie-points [root]
  (let [node (get-tiff-field GeoTIFFTagSet/TAG_MODEL_TIE_POINT root)]
    (get-tiff-doubles node)))

(defn get-model-pixel-scales [root]
  (let [node (get-tiff-field GeoTIFFTagSet/TAG_MODEL_PIXEL_SCALE root)]
    (get-tiff-doubles node)))

(defn get-geokey-dir-version [root]
  (let [geokeydir (get-tiff-field GeoTIFFTagSet/TAG_GEO_KEY_DIRECTORY root)]
    (get-tiff-short geokeydir GEO_KEY_DIRECTORY_VERSION_INDEX)))

(defn get-geokey-revision [root]
  (let [geokeydir (get-tiff-field GeoTIFFTagSet/TAG_GEO_KEY_DIRECTORY root)]
    (get-tiff-short geokeydir GEO_KEY_REVISION_INDEX)))

(defn get-geokey-minor-revision [root]
  (let [geokeydir (get-tiff-field GeoTIFFTagSet/TAG_GEO_KEY_DIRECTORY root)]
    (get-tiff-short geokeydir GEO_KEY_MINOR_REVISION_INDEX)))

(defn get-num-geokeys [root]
  (let [geokeydir (get-tiff-field GeoTIFFTagSet/TAG_GEO_KEY_DIRECTORY root)]
    (get-tiff-short geokeydir GEO_KEY_NUM_KEYS_INDEX)))

(defrecord GeoKeyRecord [keyId tagLoc count offset])

(defn get-geokey-record
  "Takes the integer key specified by a constant and the root node and returns a GeoKeyRecord"
  [keyId root]
  (when root
    (let [geokeydir (get-tiff-field GeoTIFFTagSet/TAG_GEO_KEY_DIRECTORY root)
          tiff-shorts (.getFirstChild geokeydir)
          keys (map get-int-value-attribute (node-list-seq (.getElementsByTagName tiff-shorts TIFF_SHORT_TAG)))]
    (loop [lyst (drop 4 keys)]
      (when-not (empty? lyst)
        (let [thisKeyId (first lyst)
              [loc count offset] (take 3 (rest lyst))]
          (if (= keyId thisKeyId)
            (->GeoKeyRecord thisKeyId loc count offset)
            (recur (drop 4 lyst)))))
      ))))

(defn get-geokey
  "Returns the value held in a GeoKeyRecord specified by keyId"
  [keyId root]
  (let [rec (get-geokey-record keyId root)]
    (if (zero? (:tagLoc rec))
      (:offset rec)
      (let [field (get-tiff-field (:tagLoc rec) root)]
        (when (not (nil? field))
          (let [node (.getFirstChild field)]
            (if (= TIFF_ASCIIS_TAG (.getNodeName node))
              (get-tiff-ascii node (:offset rec) (:count rec))
              (let [nlist (.getChildNodes node)
                    n (.item nlist (:offset rec))]
                (get-value-attribute n)))))))))

(defn get-dimensions [reader]
  [(.getWidth reader 0) (.getHeight reader 0)])

(defn test-one []
  (let [reader (get-reader "/home/jeff/Documents/clojure/geotiff/2012.0212.ConUS_lst_weekly.tif")
        metadata (get-metadata reader)
        rootNode (get-root-node metadata)
        directory (get-tiff-directory rootNode)
        tiff-field (get-tiff-field GeoTIFFTagSet/TAG_GEO_KEY_DIRECTORY rootNode)]
    tiff-field))

(defn -main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!"))
