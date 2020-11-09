console.log("inisde js file");

const toggleSidebar = () => {
	
	
	
	if($(".sidebar").is(":visible")){
		
		console.log("inisde if method ");
		
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		
	}else{
		
		console.log("inisde else method ");
		
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};

