(function() {

var page = document.getElementById( "hsectionchangerPage" ),
	changer = document.getElementById( "hsectionchanger" ),
	sectionChanger, idx=1;

	page.addEventListener( "pagebeforecreate", function() {

		tweetsContainer = document.getElementById('tweets_container');

		var testTweets = window.localStorage.getItem("tweets");
		var tweets = testTweets.split ("|_|");
		console.log("Test tweets: "+testTweets);

		for (var i = 0; i < tweets.length; i++) {

			var testTweetParts = tweets[i].split("-.__");
			
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
			testTweetParts[3] = testTweetParts[3].replace(/(\(.*?)?\b((?:https?|ftp|file):\/\/[-a-z0-9+&@#\/%?=~_()|!:,.;]*[-a-z0-9+&@#\/%=~_()|])/ig,
			"");

		
			
			
			tweetElement.innerHTML = testTweetParts[3];
			tweetElement.className = "tweetText"


			// Icon of favorite
			var favoriteImage = document.createElement ("img");
			favoriteImage.setAttribute ("id", "fav_img");
			favoriteImage.setAttribute ("src", "./images/fav.png");
			favoriteImage.setAttribute ("class", "tweet_action_img");

			// Text with rhe favorite count
			var favoriteText = document.createElement ("p");
			favoriteText.setAttribute ("class", "tweet_action_text");
			favoriteText.innerHTML = ""+testTweetParts[5];
			
			// Container to wrap the favorite elements
			var favoriteContainer = document.createElement ("div");
			favoriteContainer.setAttribute ("class", "tweet_action_container");
			favoriteContainer.appendChild (favoriteImage);
			favoriteContainer.appendChild (favoriteText);
			favoriteContainer.setAttribute ("onclick", "favorite(\""+testTweetParts[4]+"\")");

			// Icon of the retweet
			var retweetImage = document.createElement ("img");
			retweetImage.setAttribute ("id", "rt_img");
			retweetImage.setAttribute ("src", "./images/rt.png");
			retweetImage.setAttribute ("class", "tweet_action_img");
			
			// Text with rhe retweet count
			var retweetText = document.createElement ("p");
			retweetText.setAttribute ("class", "tweet_action_text");
			retweetText.innerHTML = ""+testTweetParts[6];
			
			// Container to wrap the retweet elements
			var retweetContainer = document.createElement ("div");
			retweetContainer.setAttribute ("class", "tweet_action_container");
			retweetContainer.appendChild (retweetImage);
			retweetContainer.appendChild (retweetText);
			retweetContainer.setAttribute ("onclick", "favorite(\""+testTweetParts[4]+"\")");
		
			// The container that align both containers as a row
			var actionsContainer = document.createElement("div");
			actionsContainer.setAttribute ("class", "ui-grid-col-2 footer");
			actionsContainer.appendChild(favoriteContainer);
			actionsContainer.appendChild(retweetContainer);
		
			// The section that wraps all the parts of the tweet
			var tweet = document.createElement("section");
			tweet.className = "tweet"
			tweet.appendChild(nameElement);
			tweet.appendChild(usernameElement);
			tweet.appendChild(timeElement);
			tweet.appendChild(tweetElement);
			tweet.appendChild(actionsContainer)
			tweetsContainer.appendChild(tweet);
		}
	});

page.addEventListener( "pagebeforeshow", function() {
	// make SectionChanger object
	sectionChanger = new tau.widget.SectionChanger(changer, {
		circular: true,
		orientation: "horizontal",
		scrollbar: "tab"
	});
});

page.addEventListener( "pagehide", function() {
	// release object
	sectionChanger.destroy();
});
			

})();
