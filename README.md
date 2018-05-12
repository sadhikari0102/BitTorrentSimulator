# BitTorrentSimulator
A peer-to-peer file transfer application

This application is designed to simulate the behaviour of peer-to-peer file transfer protocol, similar to the one used by well-known file sharing application, Bit Torrent. The application simulates multiple peer processes running in parallel, by launching multiple threads, one representing each process. For testing purposes, the threads run in the same system using localhost address, although the same behaviour can be achieved by running different threads in different systems running the same programs. PeerInfo.cfg is the configuration file which holds the configurations for all the peers to be supported by the application. 
The information is in the format : [peer ID] [host name] [listening port] [has file or not].
In the beginning, before the peers are started, atleast one of the peer should already have a file to be shared. It should be placed in the corresponding peer folder named as 'peer_peerId'.
Once all the peers are started, the file sharing starts, and at the end of it, file is replicated in all the peer's folders.
Additional configuration related to application functionalities are stored in common.cfg. Below are the test configurations:

NumberOfPreferredNeighbors 2
UnchokingInterval 5
OptimisticUnchokingInterval 15
FileName test.csv
FileSize 335872
PieceSize 16794

The protocol works on the principal of preferred neighbours and choking-unchoking. Among all the peers available to one process, it choses certain number of peers (number defined by property 'NumberOfPreferredNeighbours') to tranfer the data from. This happens on the basis of tranfer speed available from different peers. The choking and unchoking of peers and hence the determination of preferred neighbour happens periodically (period defined by 'UnchokingInterval'). 
OPTIMISTIC UNCHOKE
Each peer determines an optimistically unchoked neighbor every m seconds. We say m
is the optimistic unchoking interval. Every m seconds, peer A reselects an optimistically unchoked neighbor randomly among neighbors that are choked at that moment but are interested in its data. Then peer A sends ‘unchoke’ message to the selected neighbor and it expects to receive ‘request’ messages from it.Suppose that peer C is randomly chosen as the optimistically unchoked neighbor of peer A. Because peer A is sending data to peer C, peer A may become one of peer C’s preferred neighbors, in which case peer C would start to send data to peer A. If the rate at which peer C sends data to peer A is high enough, peer C could then, in turn, become one of peer A’s preferred neighbors. Note that in this case, peer C may be a preferred neighbor and optimistically unchoked neighbor at the same time. This kind of situation is allowed. In the next optimistic unchoking interval, another peer will be selected as an optimistically unchoked neighbor.

Additionally, the filename to be shared among peers and the size of it needs to be configured. Also, the 'piecesize' is the size of the single chunk that need to be tranfered as one.
 
HOW TO RUN:
1. Compile the application using following command:
	javac peerProcess.java
2. Run each peer individually using below command:
	java peerProcess peerId

Once all the peers are started, the file sharing continues and finishes in few seconds or minutes, depending upon the file size. At the end of it, the file should be present in all the peer folders.
