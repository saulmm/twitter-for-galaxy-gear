
function requestTweets() {
    document.getElementById('idle_screen').style.visibility = 'hidden';
    document.getElementById('loading_screen').style.visibility = 'visible';

	var timeOut = setTimeout(function () { }, 3000);
	clearTimeout(timeOut);

	window.location.assign("tab.html");
}

function onError (err) {

}