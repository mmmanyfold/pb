(ns pb.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [pb.core-test]))

(doo-tests 'pb.core-test)
