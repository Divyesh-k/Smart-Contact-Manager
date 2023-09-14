console.log("this is js file");

const toggleSidebar = () => {

       
    if($(".sidebar").is(":visible")){
         //true
         $(".sidebar").css("display" , "none");
         $(".content").css("margin-left" , "0%")
        //close
    }else{
        $(".sidebar").css("display" , "block");
        $(".content").css("margin-left" , "20%")
    }


}

const search = () => {

    let query = $("#search-input").val();

    if(query == ''){
        $(".search-result").hide();
    }else{

        //search request to server
        let url = `http://localhost:8080/search/${query}`;

        fetch(url)
        .then((Response) => {
            return Response.json();
        })
        .then((data) => {
            let text = `<div class='list-group'>`
            data.forEach((contact) => {
                text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
            })

            text += `</div>`;

            $(".search-result").html(text);
            $(".search-result").show();
        });
    }

}