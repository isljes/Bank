$(function (){

    //email alert
    $('#emailAlert').hide()
    $('#emailAlert button').click(function (){
        $('#emailAlert').hide()
    })




    //enable from onClick Edit button
    $('#editButton').click(function (){
        $('form input[disabled]').removeAttr('disabled')
        $('#saveChangesButton').removeAttr('disabled')
        $(this).attr('disabled','disabled')
    })
    //disable from onClick Save button and save changes
    $('#personal-details-form').submit(function (event){
        event.preventDefault()
        let formData=$(this).serialize()
        $.post({
            url:'http://localhost:8080/profile',
            data: formData
        }).done(function (){
            $('form input:not([disabled])').attr('disabled','disabled')
            $('#saveChangesButton').attr('disabled','disabled')
            $('#editButton').removeAttr('disabled')
        })
    })

    //send email and start countdown timer
    const countdownTimer=$('#countdown-timer')
    const btnConfirmedEmail=$('#btn-confirm-email')
    countdownTimer.hide()
    $('#form-confirm-email').submit(function (event){
        event.preventDefault()
        let formData=$(this).serialize()
        $.post({
            url:'http://localhost:8080/confirm-email',
            data: formData
        }).done(function (){
            $('#emailAlert').show()
            countdownTimer.show()
            btnConfirmedEmail.attr('disabled','disabled')
            let seconds=10
            //countdown timer

            let intervalId=setInterval(countdown,1000)

            function countdown(){
                countdownTimer.html(seconds)
                if(seconds==0){
                    clearInterval(intervalId)
                    countdownTimer.hide()
                    btnConfirmedEmail.removeAttr('disabled')
                    countdownTimer.html("")
                }
                seconds--;
            }
        })
    })


})
