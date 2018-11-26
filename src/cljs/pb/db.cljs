(ns pb.db)

(def default-db
  {:additional-id nil
   :admin-election nil
   :active-view nil
   :captcha-passed nil
   :election-slug nil
   :elections nil
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
    :es-US "Se le enviará un mensaje de texto con su un código de votación de 8 dígitos a este número de teléfono."}
   :budget
   {:en-US "Budget"
    :es-US "Presupuesto"}
   :timeline
   {:en-US "Timeline"
    :es-US "Línea de tiempo"}
   :description
   {:en-US "Description"
    :es-US "Descripción"}
   :community-impact
   {:en-US "Community Impact"
    :es-US "Impacto comunitario"}
   :error-not-found
   {:en-US "The voting code you entered is not valid. Please ensure the code is entered correctly."
    :es-US "El código no es válido. Por favor, asegúrese de que el código se ha ingresado correctamente."}
   :error-already-voted
   {:en-US "We already got your vote!"
    :es-US "Ya tenemos tu voto, gracias."}
   :ballot-recorded
   {:en-US "Ballot recorded!"
    :es-US "¡Boleta registrada!"}
   :thanks-for-voting
   {:en-US "Thanks for voting!"
    :es-US "¡Gracias por votar!\n"}
   :your-ballot-has-been-recorded
   {:en-US "Your ballot has been submitted."
    :es-US "Su boleta ha sido enviada."}
   :redirecting-to-survey
   {:en-US "Redirecting to survey..."
    :es-US "Redirigiendo a la encuesta ..."}
   :or-goto-surver-now
   {:en-US "or go to survey now"
    :es-US "O visite la encuesta haciendo clic aquí."}})
