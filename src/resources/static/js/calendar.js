$(document).ready(
    function() {
        var clsVal = $("#classroomNumber").val();
        var profVal = $("#professorName").val();
        var courseVal = $("#courseValue").val();
        var loggedInUser = $("#userName").val();
        var calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
            customButtons: {
                HomeButton: {
                    text: 'Home',
                    click: function() {
                    if(window.location.href != "http://localhost:8080/")
                        location.href = "/"
                    }
                }
            },
            headerToolbar: {
                     left: 'prev,next today,HomeButton',
                     center: 'title',
                     right: 'myMonth,myWeek'
                },
            views: {
                myDay: {
                    type: 'listDay',
                    duration: { days: 1 },
                    buttonText: ' Day '
                },
                myWeek: {
                    type: 'timeGridWeek',
                    duration: { week: 1 },
                    buttonText: ' Week '
                },
                myMonth: {
                    type: 'dayGridMonth',
                    duration: { month: 1 },
                    buttonText: ' Month '
                },
            },
            dayHeaderFormat: {
                weekday: 'short',
                month: 'numeric',
                day: 'numeric',
                omitCommas: true
            },
            slotLabelFormat: {
                hour: 'numeric',
                minute: '2-digit',
                omitZeroMinute: true,
                meridiem: 'short'
            },
            initialView: 'myWeek',
            allDaySlot: false,
            allDayText: '',
            editable: false,
            eventDurationEditable: false,
            eventStartEditable: false,
            slotDuration: "00:30:00",
            slotMinTime: '8:00',
            slotMaxTime: '22:00',
            slotLabelInterval: '01:00',
            nowIndicator: true,
            eventShortHeight: 30,
            eventMinHeight: 15,
            height: 'auto',
            eventDidMount: function(event) {
                data = event.event._def
                data_ext = data.extendedProps
                var tooltipHtml = '<h5>' + data.title + '</h5>' +
                    '<p><b>Classroom:</b> ' + data_ext.classroom + '</p>' +
                    '<p><b>Lecturer:</b> ' + data_ext.lecturer + '</p>' +
                    '<p><b>Name:</b> ' + data_ext.description + '</p>' +
                    '<p><b>Duration:</b> ' + data_ext.startStr + ' - ' + data_ext.endStr + '</p>'
                    ;

                $(event.el).tooltip({
                    placement: 'auto bottom',
                    overflow: 'visible',
                    position: 'relative',
                    html: true,
                    delay: { "show": 300, "hide": 0 },
                    container: 'body',
                    title: tooltipHtml
                });
            },
        });
        if ( $('#userName').val() ){
            if($('#professorName').val()){
                calendar.addEventSource('/api/professor/' + profVal);
                calendar.render();
            }
            else if($('#classroomNumber').val()){
                calendar.addEventSource('/api/classroom/' + clsVal);
                calendar.render();
            }
            else if($('#courseValue').val()){
                calendar.addEventSource('/api/course/' + courseVal);
                calendar.render();
            }
            else {
                calendar.addEventSource('/api/allevents/' + loggedInUser);
                calendar.render();
            }
        }
       else if( $('#professorName').val() )
        {
            calendar.addEventSource('/api/professor/' + profVal);
            calendar.render();
        }
        else if( $('#classroomNumber').val() )
        {
            calendar.addEventSource('/api/classroom/' + clsVal);
            calendar.render();
        }
        else if( $('#courseValue').val() ){
           calendar.addEventSource('/api/course/' + courseVal);
           calendar.render();
       }
        else
        {
            calendar.addEventSource('/api/allevents');
            calendar.render();
         }
    }
);