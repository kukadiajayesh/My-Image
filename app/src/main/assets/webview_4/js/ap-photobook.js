/*
 * Magazine sample
*/
function updateDepth(book, newPage, bookpages) {
    var page = book.turn('page'),
        pages = book.turn('pages'),
        depthWidth = 16*Math.min(1, page*2/pages);

    newPage = newPage || page;    
    var cente = jQuery(window).width();	
    jQuery('.left-depth').css({width: 0});

    if (newPage>3)
	jQuery('.left-depth').css({
            width: depthWidth,
            left: -1 - depthWidth
	});
    else
	jQuery('.left-depth').css({width: 0});
    
    depthWidth = 16*Math.min(1, (pages-page)*2/pages);
    if (newPage<pages-3)
    {	
	jQuery('.right-depth').css({
            width: depthWidth,
            right: -1 - depthWidth
	});
    }
    else
	jQuery('.right-depth').css({width: 0});
}

function addPage(page, book) {
   
	var id, pages = book.turn('pages');
        
	if (!book.turn('hasPage', page)) {
            if(page == 1 || page == 2 || page == AP_TotalPages || page == AP_SecondLast)
                var element = jQuery('<div />',{'class': 'hard'}).html('<div class="loader"></div>');
            else
                var element = jQuery('<div />').html('<div class="loader"></div>');
            
            
            if (book.turn('addPage', element, page)) 
		loadPage(page,element);
	}
}

function loadPage(page, pageElement) {
	
//    if(page == 1)
//        var img_name = "front-cover.jpg";
//    else
    var img_name = page+".jpg";		

    var img = jQuery('<img />');
    var str=AP_ImageUrl+img_name;
    pageElement.css('background-size', '100% 100%');
    pageElement.css('background-position', '0% 0%');
    if(Ap_Display == "0") {
      if(page == 1 || page == AP_TotalPages) {  }
      else {
        if((page%2) == 0) {
            var str=AP_ImageUrl+img_name; 
            pageElement.css('background-size', '200% 100%');
            pageElement.css('background-position', '0% 0%');
        }else{
            img_name = (page-1)+".jpg"
            var str=AP_ImageUrl+img_name;    
            pageElement.css('background-size', '200% 100%');
            pageElement.css('background-position', '100% 0%');
        }            
       }  
    }    
      
    
    img.attr('src',str);
    pageElement.css('background-image', 'url('+str+') ');
    pageElement.css('background-repeat', 'no-repeat');	
    
    var divImg=jQuery('<div class="gradient"></div>');
    
    if((page%2) == 0) {
        var ext_class="ap_left_page_num";	
        var ext_id = 'ap_lpage_num_id_'+page;
    } else{
        var ext_class="ap_right_page_num";
        var ext_id = 'ap_rpage_num_id_'+page;
    }    
       
    //if(page)
 
    if(page == 1) {
        if(Ap_EnableComm == 'yes') 
            var pagenumberEle=jQuery('');
        else
            var pagenumberEle=jQuery('');
    
    } else if(page == AP_TotalPages) {
        if(Ap_EnableComm == 'yes') 
            var pagenumberEle=jQuery('');
        else
            var pagenumberEle=jQuery('');
        
    } else {
        
        if(Ap_EnableComm == 'yes') 
            var pagenumberEle=jQuery('');
        else
                var pagenumberEle=jQuery('');
       
    }
    
    img.load(function() {
       // pagenumberEle.appendTo(pageElement.closest('.page-wrapper'))
        divImg.appendTo(pageElement);
        pageElement.find('.loader').remove();
    });
}

// Zoom in / Zoom out

function zoomTo(event) {
		
    setTimeout(function() {
        if (jQuery('.magazine-viewport').data().regionClicked) {
                jQuery('.magazine-viewport').data().regionClicked = false;
        } else {
                if (jQuery('.magazine-viewport').zoom('value')==1) 
                    jQuery('.magazine-viewport').zoom('zoomIn', event);
                else 
                    jQuery('.magazine-viewport').zoom('zoomOut');
        }
    }, 1);
}

//function ap_commens(id,page) {
//    jQuery('#ap_comment_model').modal();
//    jQuery('#ap_add_comment_form')[0].reset();
//    jQuery.ajax({
//        type:'POST',
//        url: AP_URL.ajax,
//        data:{ action: "ap_get_page_comment", photobook : id ,'pageid' : page, 'nonce' : jQuery("#ap_get_comment_nonce").val() },
//        dataType: 'json',
//        success:function(data){
//           jQuery('.ap_comment_section').show();
//           jQuery("#ap_add_comment_form").find('#ap_photobook_id').val(id);
//           jQuery("#ap_add_comment_form").find('#ap_page_id').val(page);
//           if(data.error == false) {
//               jQuery('.ap_comment_section').html(data.comments);
//               jQuery('.ap_comment_section').scrollTop(jQuery('.ap_comment_section')[0].scrollHeight);
//           } else {
//               jQuery('.ap_comment_section').html(data.message);
//           }
//        }            
//    });
//}

function isChrome() {
    return navigator.userAgent.indexOf('Chrome')!=-1;
}

function disableControls(page) {
		
}

function ap_pagenumber(page) {
      
    var page = page || jQuery('.magazine').turn('page');
    var offset = 0.25*jQuery('.magazine').width() + 'px';
   
    if(Ap_EnableComm == 'yes') 
        var pagenumDiv = jQuery('<div class="ap_pagenumber_main"><div class="ap_left_page_num"><div class="ap_comment_scection" ><a href="javascript: void(0)" title="Add Comment" class="ap_comment_element_click" data-pbid="'+AP_PhotoBookID+'" data-pageid=""  ><i class="fa fa-comment-o" aria-hidden="true"></i></a></div><div class="ap_page_title_cection"><a href="javascript: void(0)" class="appagetitle" ></a></div></div><div class="ap_right_page_num"><div class="ap_comment_scection" ><a href="javascript: void(0)" class="ap_comment_element_click" title="Add Comment" data-pbid="'+AP_PhotoBookID+'" data-pageid="" ><i class="fa fa-comment-o" aria-hidden="true"></i></a></div><div class="ap_page_title_cection"><a href="javascript: void(0)" class="appagetitle" ></a></div></div></div>');
    else
        var pagenumDiv = jQuery('<div class="ap_pagenumber_main"><div class="ap_left_page_num" style="text-align:center;"><a href="javascript: void(0)" class="appagetitle" ></a></div><div style="text-align:center;" class="ap_right_page_num"><a href="javascript: void(0)" class="appagetitle" ></a></div></div>');

    if (jQuery('.ap_pagenumber_main').length === 0) {
        pagenumDiv.appendTo(jQuery('.ap-book'));
    }
   
    if (page == 1) {
       
      jQuery(".ap_right_page_num").find('.ap_comment_element_click').attr('data-pageid',page);  
      jQuery(".ap_left_page_num").hide();
      //jQuery(".ap_right_page_num a").css("margin-left","-28px");
      //jQuery(".ap_right_page_num a.appagetitle").html("Front Cover");
      offset = '-' + offset;
    }
    
    else if (page != AP_TotalPages) {
       // if(Ap_Display == "1") {
              if((page%2) == 0) {
                var pg1=page;
                var pg2=page+1;
              } else {
                var pg1=page-1;
                var pg2=page;  
              } 
            
            jQuery(".ap_right_page_num").find('.ap_comment_element_click').attr('data-pageid',pg2);  
            jQuery(".ap_left_page_num").find('.ap_comment_element_click').attr('data-pageid',pg1);  
            
           // jQuery(".ap_right_page_num a.appagetitle").html(pg2);
           // jQuery(".ap_left_page_num a.appagetitle").html(pg1);

           jQuery(".ap_left_page_num").show();
           jQuery(".ap_right_page_num").show();
//           jQuery(".ap_left_page_num a").css("margin-right",(jQuery('.magazine').width()/4));
//            jQuery(".ap_right_page_num a").css("margin-left",(jQuery('.magazine').width()/4));
//        }    
//        else {
//            var pgno = page;
//            if(page > 2) {
//                if(page%2 == 0){
//                    var curentpg = Math.ceil((page/2));
//                    var pgno = curentpg+1;
//                }   
//                else {
//                    var pgno = Math.ceil((page/2));
//                }    
//            }    
//            jQuery(".ap_right_page_num").find('.ap_comment_element_click').attr('data-pageid',pgno);  
//            jQuery(".ap_right_page_num a.appagetitle").html(pgno);
//            
//            jQuery(".ap_left_page_num").hide();
//            jQuery(".ap_right_page_num").show();
//            //jQuery(".ap_right_page_num a").css("margin-left","-28px");
//        }
      offset = 0;
    }
    else if(page == AP_TotalPages){
      jQuery(".ap_right_page_num").hide();
      jQuery(".ap_left_page_num").find('.ap_comment_element_click').attr('data-pageid',page);    
      //jQuery(".ap_right_page_num a").css("margin-left","-28px");
      //jQuery(".ap_left_page_num a.appagetitle").html("Back Cover");    
       //jQuery(".ap_right_page_num a").css("margin-left","-28px");
    }
    
    

 }

//function ap_pagenumber(page) {
//      
//    var page = page || jQuery('.magazine').turn('page');
//    var offset = 0.25*jQuery('.magazine').width() + 'px';
//    
//    
//    if (page == 1) {
//            jQuery(".ap_left_page_num").hide();
//            //jQuery(".ap_page_num_id_"+page+" a").css("margin-left","-28px");
//            jQuery(".ap_right_page_num a.appagetitle").html("Front Cover");
//            jQuery(".ap_right_page_num").show();
//        offset = '-' + offset;
//    }
//    else if (page != AP_TotalPages) {
//        if(Ap_Display == "1") {
//            var pg1=page;
//            var pg2=page+1;
//            jQuery(".ap_right_page_num a.appagetitle").html(pg2);
//            jQuery(".ap_left_page_num a.appagetitle").html(pg1);
//
//           jQuery(".ap_left_page_num").show();
//           jQuery(".ap_right_page_num").show();
//           //jQuery(".ap_left_page_num a").css("margin-right",(jQuery('.magazine').width()/4));
//            //jQuery(".ap_right_page_num a").css("margin-left",(jQuery('.magazine').width()/4));
//        }    
//        else {
//            var pgno = page;
//            if(page > 2) {
//                if(page%2 == 0){
//                    var curentpg = Math.ceil((page/2));
//                    var pgno = curentpg+1;
//                }   
//                else {
//                    var pgno = Math.ceil((page/2));
//                }    
//            }    
//            
//            jQuery(".ap_right_page_num a.appagetitle").html(pgno);
//            
//            jQuery(".ap_right_page_num").show();
//           // jQuery(".ap_right_page_num a").css("margin-left","-28px");
//        }
//      offset = 0;
//     } 
//    else if(page == AP_TotalPages){
//       jQuery(".ap_left_page_num a").html("Back Cover");
//       jQuery(".ap_right_page_num").hide();
//      // jQuery(".ap_right_page_num a").css("margin-left","-28px");
//    }
//
// }

// Set the width and height for the viewport
function resizeViewport() {
    
    var width = jQuery(window).width(),//-40,
	height = jQuery(window).height(),
	options = jQuery('.magazine').turn('options');
    
    //height=height-40;
    if(!IsMobile)
    {
        height=height-0;//-90;
        if(ThumbsStatus)
            height=height-34;//-90;
    }
    
    jQuery('.magazine').removeClass('animated');
    jQuery('.magazine-viewport').css({
	width: width,
	height: height
    }).zoom('resize');
   
    if (jQuery('.magazine').turn('zoom')==1) {
        var bound = calculateBound({
            width: options.width,
            height: options.height,
            boundWidth: Math.min(options.width, width),
		boundHeight: Math.min(options.height, height)
            });

            if (bound.width%2!==0)
		bound.width-=1;
		
    	if (bound.width!=jQuery('.magazine').width() || bound.height!=jQuery('.magazine').height()) {
            bound.width=bound.width-40;
            jQuery('.magazine').turn('size', bound.width, bound.height);
        }
	jQuery('.magazine').css({top: -bound.height/2, left: -(bound.width)/2});
    }
    
    var magazineOffset = jQuery('.magazine').offset(),
	boundH = height - magazineOffset.top - jQuery('.magazine').height(),
	marginTop = (boundH - jQuery('.thumbnails > div').height()) / 2;

    if (marginTop<0) 
	jQuery('.thumbnails').css({height:1});
    else {
	jQuery('.thumbnails').css({height: boundH});
	jQuery('.thumbnails > div').css({marginTop: marginTop});
    }
    
    if (magazineOffset.top<jQuery('.made').height())
	jQuery('.made').hide();
    else
	jQuery('.made').show();
    
    ap_pagenumber()
    
    jQuery('.magazine').addClass('animated');
    
    PhotobookBookLoded=true;
    if(Apdelayed)ApcloseLoader();
	
}


// Number of views in a flipbook

function numberOfViews(book) {
    return book.turn('pages') / 2 + 1;
}

// Current view in a flipbook

function getViewNumber(book, page) {
    return parseInt((page || book.turn('page'))/2 + 1, 10);
}

function moveBar(yes) {
    if (Modernizr && Modernizr.csstransforms) {
        jQuery('#slider .ui-slider-handle').css({zIndex: yes ? -1 : 10000});
    }
}

function largeMagazineWidth() {	
	return 2214;
}

function decodeParams(data) {

    var parts = data.split('&'), d, obj = {};

    for (var i =0; i<parts.length; i++) {
        d = parts[i].split('=');
        obj[decodeURIComponent(d[0])] = decodeURIComponent(d[1]);
    }

    return obj;
}

function calculateBound(d) {
	
    var bound = {width: d.width, height: d.height};

    if (bound.width>d.boundWidth || bound.height>d.boundHeight) {
		
	var rel = bound.width/bound.height;

        if (d.boundWidth/rel>d.boundHeight && d.boundHeight*rel<=d.boundWidth) {
                bound.width = Math.round(d.boundHeight*rel);
                bound.height = d.boundHeight;
        } else {
                bound.width = d.boundWidth;
                bound.height = Math.round(d.boundWidth/rel);
        }
    }
		
    return bound;
}