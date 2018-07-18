(ns blockchain-tcp.meshwork)

(def candidateBlock 'a channel to propose block)

(def announcement 'a channel to broadcast latest blockchain)

(def mutex 'standard variable that allows us to control reads/writes and prevent data races)

(def validators 'map of nodes and the amount of tokens they’ve staked)
