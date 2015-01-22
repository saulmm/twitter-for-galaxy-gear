
 
var SAAgent = null;
var SASocket = null;
var CHANNELID = 104;
var ProviderAppName = "HelloAccessoryProvider";
var callback;
var myCallback;
var eCallback;

function setConnectedCallback (connectedCallback) {
	callback = connectedCallback;
}

function setErrorCallback (errorCallback) {
	eCallback = errorCallback;
}




function fetch(order) {

	try {
		console.log ("The order is... "+order);
		console.log ("The SASocket state is: "+SASocket);
		SASocket.setDataReceiveListener(onreceive);
		SASocket.sendData(CHANNELID, order);

	} catch(err) {

		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

var agentCallback = {
	
	onconnect : function(socket) {

		SASocket = socket;
		console.log("Connection established with RemotePeer");
		callback();

		SASocket.setSocketStatusListener(function(reason){
			console.log("Service connection lost, Reason : [" + reason + "]");
			disconnect();
		});
	},
	onerror : onerror
};

var peerAgentFindCallback = {
	
onpeeragentfound : function(peerAgent) {
		
		try {
		
			if (peerAgent.appName == ProviderAppName) {
				SAAgent.setServiceConnectionListener(agentCallback);
				SAAgent.requestServiceConnection(peerAgent);
		
			} else {
		
				alert("Not expected app!! : " + peerAgent.appName);
			}
		
		} catch(err) {
			console.log("exception [" + err.name + "] msg[" + err.message + "]");
		}
	},
	onerror : onerror
}

function onsuccess(agents) {
	
	try {	
		if (agents.length > 0) {
		
			SAAgent = agents[0];	
			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			SAAgent.findPeerAgents();

		} else {

			console.log ("SAAgent not found...");
		}

	} catch(err) {

		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function onerror (error) {
	
	eCallback(error);
	console.log("err [" + err.name + "] msg[" + err.message + "]");
}

function connect() {
	
	if (SASocket) {
	
		console.log ("The socket is already connected");
        return false;
    }

	try {
	
		webapis.sa.requestSAAgent(onsuccess, onerror);
	
	} catch (err) {
	
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}


function disconnect() {
	try {
		if (SASocket != null) {
			SASocket.close();
			SASocket = null;
			createHTML("closeConnection");
		}
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function onreceive(channelId, data) {
	
	console.log ("raw data: "+data)
	
	if (data.substring(0, 4) === "__tw") {
		
		var tweets = data.split('|=|')[1];
		
		window.localStorage.setItem("tweets", tweets); 
		window.location.assign("tweets_screen.html");
	
	} else if (data === "/error/not_logged") {
		eCallback("NOT_LOGGED");
	}
}

window.onload = function () {
    // add eventListener for tizenhwkey
    document.addEventListener('tizenhwkey', function(e) {
        if(e.keyName == "back")
            tizen.application.getCurrentApplication().exit();
    });
};
