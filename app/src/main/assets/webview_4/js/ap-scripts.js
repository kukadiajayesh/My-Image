jQuery(document).ready(function($) {	
    $(document).bind('keydown', function(event) {
     if (!( String.fromCharCode(event.which).toLowerCase() == 's' && event.ctrlKey) && !(event.which == 19)) return true;

      event.preventDefault();
      return false;
    });
    $(document).bind("contextmenu",function(e){
          return false;
    });
});
jQuery(document).keydown(function(event) {
    if (!( String.fromCharCode(event.which).toLowerCase() == 'u' && event.ctrlKey) && !(event.which == 19)) return true;
    
    event.preventDefault();
    return false;
});
jQuery(document).click(function(e) {
    if (e.shiftKey) {
        e.stopPropagation();
        return false;
    } 
});