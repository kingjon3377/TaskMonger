doc "An annotation, like \"doc\", to record TODOs."
todo "When the model includes ConstrainedAnnotation, switch to that."
shared Nothing todo(String description) { return null; }

doc "An annotation, like \"doc\", to record FIXMEs."
todo "When the model includes ConstrainedAnnotation, switch to that."
shared Nothing fixme(String description) { return null; }

doc "An annotation to document parameters."
todo "When the model includes ConstrainedAnnotation, switch to that."
shared Nothing param(String name, String description) { return null; }