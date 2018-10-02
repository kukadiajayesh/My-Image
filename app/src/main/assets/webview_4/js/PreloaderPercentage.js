    var ap_timer = null;
    jQuery(document).ready(function ($) {
    "use strict"
    
    if (!Array.prototype.indexOf){
      Array.prototype.indexOf = function(elt /*, from*/){
        var len = this.length >>> 0;

        var from = Number(arguments[1]) || 0;
        from = (from < 0)
             ? Math.ceil(from)
             : Math.floor(from);
        if (from < 0)
          from += len;

        for (; from < len; from++){
          if (from in this &&
              this[from] === elt)
            return from;
        }
        return -1;
      };
    }

    var bgImg = [], img = [], count=2, percentage = 0;       

    ap_timer =  setInterval(function(){completePhotobookLoad()},500);
    
    var afterDelay = function () 
    {
	Apdelayed=true;
	completePhotobookLoad();	
    };
    
    var startTimer = function () {
	setTimeout(afterDelay, 5000);
    };   
    startTimer();

    $('*').filter(function() {

        var val = $(this).css('background-image').replace(/url\(/g,'').replace(/\)/,'').replace(/"/g,'');
        var imgVal = $(this).not('script').attr('src');

        if(val !== 'none' && !/linear-gradient/g.test(val) && bgImg.indexOf(val) === -1){
            bgImg.push(val)
        }

        if(imgVal !== undefined && img.indexOf(imgVal) === -1){
            img.push(imgVal)
        }

    });
    function completePhotobookLoad(){
        count++;
        percentage = Math.floor(count * 10);
	if(percentage>95)percentage=92;
        $('.ap_loader_percentage').html('<span>'+percentage + '%'+'</span>');
	if(Apdelayed && PhotobookBookLoded)
	{
	    $('.ap_loader_percentage').html('<span>98%</span>');
	    ApcloseLoader();
        }
    }

    });

    function ApcloseLoader()
    {
      clearTimeout(ap_timer);
      jQuery('#ap_preloading').fadeOut();
      setTimeout(function(){
          jQuery(".ap_photobook_content").fadeIn(100); 
          app.onLoadSuccess();
          if(APAppAutoplay == 1)
             ap_autoplay();
            
      }, 100);
      
    }