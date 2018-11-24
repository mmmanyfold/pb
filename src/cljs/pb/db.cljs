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
   :language-in-view :es-US})
