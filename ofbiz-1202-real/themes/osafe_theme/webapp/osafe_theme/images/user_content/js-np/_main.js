jq11(document).ready(function() {

	var mainNavUl = jq11( ".nav li.dropdown > ul" ),
	mainNavA = jq11( ".nav li.dropdown > a" ),
	allSliders = jq11("#myCarousel");

	mainNavUl
	  .mouseenter(function() {
	    jq11( this ).siblings().addClass( "hover" );
	  })
	  .mouseleave(function() {
	    jq11( this ).siblings().removeClass( "hover" );
	  });

	// disable hrefs on main nav links
	mainNavA
		.click(function( event ) {
		  event.preventDefault();
		});

	/*
    THIS CAUSES JS ERROR - maybe need to add new jq11 variable to this JS TOuch library
    allSliders.swiperight(function() {  
	      jq11(this).carousel('prev');  
	    });  
   allSliders.swipeleft(function() {  
	      jq11(this).carousel('next');
	   }); */

   
/*
    jq11(".swapPng").hover(function(){
        //var over = logoImgSrc.replace(/.png$/gi,"-alt.png");
        swapSrc = jq11(this).attr("src");
        //jq11(this).attr("src",over);
        console.log(swapSrc);
        //alert("hi");
    });
*/



    var swapJpg = jq11(".swapJpg");

	swapJpg.each(function(){

		var swapJpgSrc = jq11(this).attr("src")
        
        // add mouseover
        jq11(this).mouseover(function(){
            
            var over = swapJpgSrc.replace(/.jpg$/gi,"-alt.jpg");

            jq11(this).attr("src",over);
            //console.log(over);
        });

        // add mouse out
        jq11(this).mouseout(function(){
            jq11(this).attr("src",swapJpgSrc);
        });
    });



    var swapPng = jq11(".swapPng");

	swapPng.each(function(){

		var swapPngSrc = jq11(this).attr("src")
        
        // add mouseover
        jq11(this).mouseover(function(){
            
            var over = swapPngSrc.replace(/.png$/gi,"-alt.png");

            jq11(this).attr("src",over);
            //console.log(over);
        });

        // add mouse out
        jq11(this).mouseout(function(){
            jq11(this).attr("src",swapPngSrc);
        });
    });




});