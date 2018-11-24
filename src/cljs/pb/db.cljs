(ns pb.db)

(def default-db
  {:admin-election nil
   :active-view nil
   :captcha-passed nil
   :election-slug nil
   :elections nil
   :election-in-view nil
   :proposals-in-view nil
   :selected-proposals []
   :voter-id nil
   :admin false
   ;; new structure for translated content entries
   :entries nil
   ;; options are :en-US and :es-US
   :language-in-view :en-US
   :election-in-view-2 nil})
