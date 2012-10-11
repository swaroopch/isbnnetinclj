
jQuery.runisbn = function() {

  if ((/isbn.net.in/i).test(window.location.href)) {
    alert("Please drag the link to your browser's bookmarks toolbar.");
    return;
  }

  // Check if a given string is in valid ISBN format (ISBN-10 or ISBN-13)
  function isISBN(text) {
    return (/[0-9]{9}[0-9xX]/).test(text) || (/^[0-9]{13}$/).test(text);
  }

  // Go to page showing prices
  function show_prices(isbn) {
    var url = 'http://isbn.net.in/' + isbn;
    document.location.href = url;
  }

  ///// Figure out ISBN /////
  var isbn;
  if ((/infibeam.com\/Books\/info/i).test(window.location.href)) {
    isbn = $.trim( $("b:contains('EAN:')").parent().next().children("h2").text() );
    if (isbn.length === 0) {
      isbn = $.trim( $("b:contains('ISBN:')").parent().next().children("h2").text() );
    }
  } else if ((/flipkart.com/i).test(window.location.href)) {
    isbn = $.trim( $("td:contains('ISBN-13:')").next().find("b h2:first").text() );
    if (isbn.length === 0) {
      isbn = $.trim( $("td:contains('ISBN:')").next().find("b h2:first").text() );
    }
  } else if ((/indiaplaza.com\/.*\/books\/.*/i).test(window.location.href)) {
    isbn = $.trim( $("span[itemprop=isbn] span.greyFont").text() );
  } else if ((/amazon.com/i).test(window.location.href)) {
    var node = jQuery("b:contains('ISBN-13:')");
    if (node.length) {
      isbn = jQuery.trim( node.parent().text().match(/[\dxX-]+$/)[0] );
    }
  } else if ((/shop.oreilly.com/i).test(window.location.href)) {
    isbn = jQuery.trim( jQuery("dd.isbn:first").text() );
  } else if ((/pragprog.com/i).test(window.location.href)) {
    isbn = $.trim( $("span[itemprop=isbn]").text() );
  } else {
    var fulltext = document.getElementsByTagName("body")[0].innerHTML;
    var grepforisbn = fulltext.match(/ISBN.*?(\d{9}[0-9xX])/i);
    if (grepforisbn === null) {
      grepforisbn = fulltext.match(/ISBN.*?(\d{13})/i);
    }
    if (grepforisbn !== null) {
      isbn = grepforisbn[1];
    }
    if (! isISBN(isbn) ) {
      isbn = undefined;
    }
  }

  if ( typeof(isbn) !== 'undefined' || typeof(isbn) !== 'null') ) {
    isbn = isbn.replace(/-/g, '');
    if ( isISBN(isbn) ) {
      show_prices(isbn);
    } else {
      alert("No ISBN or EAN found");
    }
  } else {
    alert("No ISBN or EAN found");
  }

};

// vim: tabstop=2 shiftwidth=2
