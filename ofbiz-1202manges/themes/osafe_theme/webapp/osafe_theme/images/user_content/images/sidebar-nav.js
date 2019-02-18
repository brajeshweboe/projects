$(document).ready(function(){												

  	// Initialize navgoco with default options
    $(".main-menu").navgoco({
        caret: '<span class="caret"></span>',
        accordion: false,
        openClass: 'open',
        save: true,
        cookie: {
            name: 'navgoco',
            expires: false,
            path: '/'
        },
        slide: {
            duration: 300,
            easing: 'swing'
        }
    });

    //Navigation Menu Slider - https://github.com/tefra/navgoco
    $('#nav-expander').on('click',function(e){
      e.preventDefault();
      $('body').toggleClass('nav-expanded');
      $('#navOverlay').fadeIn(300);
      //$('#disableLink').show(); uncomment to disable home link on logo when nav is open
    });
    $('#nav-close,#navOverlay,#disableLink').on('click',function(e){
      e.preventDefault();
      $('body').removeClass('nav-expanded');
      $('#navOverlay').fadeOut(300);
      //$('#disableLink').hide(); uncomment to disable home link on logo when nav is open
    });
    	
});