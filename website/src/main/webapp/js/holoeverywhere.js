var Holo = {
	init : function() {
		this.includeCss("css/style.css");
		
		return this;
	},
	includeCss : function(url) {
		$("<link>", {
			rel: "stylesheet",
			href: url,
			appendTo: $("head")
		});
	}
}.init();