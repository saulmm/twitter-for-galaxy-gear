(function() {

var page = document.getElementById( "hsectionchangerPage" ),
	changer = document.getElementById( "hsectionchanger" ),
	sectionChanger, idx=1;

	page.addEventListener( "pagebeforecreate", function() {

		console.log("Executing section changer before create function");
		tweetsContainer = document.getElementById('tweets_container');

		console.log("Trying to get tweets from local store");
		var testTweets = window.localStorage.getItem("tweets");
		console.log("Tweets obtained: "+testTweets);

		var tweets = testTweets.split ("|_|");

		for (var i = 0; i < tweets.length; i++) {

			var testTweetParts = tweets[i].split("-.__");

			console.log ("Adding tweet page");
			
			var nameElement = document.createElement("p");
			nameElement.innerHTML = testTweetParts[0];
			nameElement.className = "tweetName"

			var timeElement = document.createElement("p");
			timeElement.innerHTML = testTweetParts[1];
			timeElement.className = "tweetTime"

			var usernameElement = document.createElement("p");
			usernameElement.innerHTML = testTweetParts[2];
			usernameElement.className = "tweetUsername"

			var tweetElement = document.createElement("p");

			testTweetParts[3] = testTweetParts[3].replace(/@(\w+)/g,"<font color='85CBFF'>@$1</font>")
			testTweetParts[3] = testTweetParts[3].replace(/#(\w+)/g,"<font color='85CBFF'>@$1</font>")

			tweetElement.innerHTML = testTweetParts[3];
			tweetElement.className = "tweetText"

			var tweet = document.createElement("section");
			tweet.className = "tweet"
			tweet.appendChild(nameElement);
			tweet.appendChild(usernameElement);
			tweet.appendChild(timeElement);
			tweet.appendChild(tweetElement);
			tweetsContainer.appendChild(tweet);
		}
	});

page.addEventListener( "pagebeforeshow", function() {
	// make SectionChanger object
	sectionChanger = new tau.widget.SectionChanger(changer, {
		circular: false,
		orientation: "horizontal",
		useBouncingEffect: true
	});
});

page.addEventListener( "pagehide", function() {
	// release object
	sectionChanger.destroy();
});

})();
