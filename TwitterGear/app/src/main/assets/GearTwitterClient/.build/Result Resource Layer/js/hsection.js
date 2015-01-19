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

			tweetElement.innerHTML = testTweetParts[3];
			tweetElement.className = "tweetText"

			var div_grid = document.createElement("div");
			div_grid.className = "ui-grid-row";

			var buttonFavorite = document.createElement("a"); 
			buttonFavorite.setAttribute ("class", "ui-btn");
			buttonFavorite.setAttribute ("onclick", "favorite(\""+testTweetParts[4]+"\")");
			buttonFavorite.innerHTML = "Favorite";
			div_grid.appendChild(buttonFavorite);

			var buttonRetweet = document.createElement("a"); 
			buttonRetweet.setAttribute ("class", "ui-btn");
			buttonRetweet.innerHTML = "Retweet";
			buttonRetweet.setAttribute ("onclick", "retweet(\""+testTweetParts[4]+"\")");
			div_grid.appendChild(buttonRetweet);
			
			
			var buttonDiv = document.createElement("div");
			buttonDiv.setAttribute ("class", "ui-grid-col-3 button-group-height");
			var rt_img = document.createElement("img");
			rt_img.setAttribute ("src", "./images/rt.png");
			
			var fav_img = document.createElement("img");
			fav_img.setAttribute ("src", "./images/fav.png");
			
			buttonDiv.appendChild(rt_img);
			buttonDiv.appendChild(fav_img);

			var tweet = document.createElement("section");
			tweet.className = "tweet"
			tweet.appendChild(nameElement);
			tweet.appendChild(usernameElement);
			tweet.appendChild(timeElement);
			tweet.appendChild(tweetElement);
			//tweet.appendChild(buttonDiv);
//			tweet.appendChild(div_grid)

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
