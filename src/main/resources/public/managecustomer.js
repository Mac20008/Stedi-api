//© 2021 Sean Murdock

let customerName = "";
let phone = "";
let bday = "";
let form = "";
let elements = "";

function setcustomername(){
    customerName = $("#cn").val();
}

function setemail(){
    email = $("#email").val();
}

function setphone(){
    phone = $("#phone").val().replace(/\D+/g, "");
}

function setbday(){
    bday = $("#bday").val();
}


function readonlyforms(formid){
    form = document.getElementById(formid);
    elements = form.elements;
    for (i = 0, len = elements.length; i < len; ++i) {
    elements[i].readOnly = true;
    }
    createbutton();
}
 function pwsDisableInput( element, condition ) {
        if ( condition == true ) {
            element.disabled = true;

        } else {
            element.removeAttribute("disabled");
        }

 }

function createbutton(){
    var button = document.createElement("input");
    button.type = "button";
    button.value = "OK";
    button.onclick = window.location.href = "/index.html";
    context.appendChild(button);
}

function findcustomer(email){
    var headers = { "suresteps.session.token": localStorage.getItem("token")};
    $.ajax({
        type: 'GET',
        url: `/customer/${email}`,
        contentType: 'application/text',
        dataType: 'text',
        headers: headers,
        success: function(data) {
            localStorage.setItem("customer",data);
            window.location.href="/timer.html";
        }
    });
}

function createcustomer(){
    //in case they hit the back/forward buttons and our in memory variables got reset
    setusername();
    setuserpassword();
    setverifypassword();
    setcustomername();
    setemail();
    setphone();
    setbday();

//this is the more picky of the two operations, so let's try it first, and if it succeeds, create the customer, not vice
// versa

    $.ajax({
        type: 'POST',
        url: '/user',
        data: JSON.stringify({'userName':email, email, password, phone, "birthDate":bday, verifyPassword}),//we are using the email as the user name
        success: function(data) {
            createCustomer(data);
        },
        error: function(xhr){
            console.log(JSON.stringify(xhr))
            if(xhr.status==409){
                alert("Email or cell # has already been previously registered");
            } else{
                alert("Error creating account. Please confirm password is at least 6 characters, has an upper case letter, a lower case letter, a number, and a symbol.")
            }
        },
        contentType: "application/text",
        dataType: 'text'
    });


}

const createCustomer = (createUserResponse)=>{

    const customer = {
        customerName : customerName,
        email : email,
        phone : phone,
        birthDay: bday
    }

    $.ajax({
        type: 'POST',
        url: '/customer',
        data: JSON.stringify(customer),
        contentType: 'application/text',
        dataType: 'text',
        success: function (data) {
            localStorage.setItem("customer", JSON.stringify(customer));
            alert(createUserResponse);
            window.location.href = "/index.html"
        },
        error: function (xhr) {
            console.log(JSON.stringify(xhr))
            if (xhr.status == 409) {
                alert("Email or cell # has already been previously registered");
            } else {
                alert("Error creating account. Please confirm information is correct.")
            }
        },
    });
}

