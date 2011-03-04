(ns cleavage.repository)

(defprotocol ScmRepository
  (revisions [repo])
  (files [repo])
  (relative-path [repo file])
  (revision-number [repo revision])
  (commits [repo file revision])
  (revision-contents [repo file revision]))
