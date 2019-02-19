
		jQuery(document).ready(function (){
		   // Array holding selected row IDs
			jQuery('#sortableData').DataTable( {
               	searching: false,
               	"pageLength": 20,
  		        lengthMenu: [ 5, 10, 20, 50],
  		        dom: 'Bfrt<lp<t>ifp>',
		        buttons: [
		            'pdf', 'excel', 
		              ],
		
		    });	
		  
		   });