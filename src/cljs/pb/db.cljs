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

(def translations-db
  {:continue
   {:en-US "CONTINUE"
    :es-US "CONTINUAR"}
   :enter-8-digit-code
   {:en-US "Enter the 8-digit code:"
    :es-US "Ingrese el código de 8 dígitos"}
   :check-your-text-msgs
   {:en-US "Check your text messages!"
    :es-US "¡Revisa tus mensajes de texto!"}
   :text-message
   {:en-US "A text message with an 8-digit voting code will be sent to this phone number."
    :es-US "Se le enviará un mensaje de texto con su un código de votación de 8 dígitos a este número de teléfono."}})