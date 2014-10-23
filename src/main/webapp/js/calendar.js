(function(){
	function Calendar(container){
		this.container = $(container);
		this.events = {};
		this.active = {};
		this.init();
	};
	Calendar.prototype = {
		constructor : Calendar,
		now : new Date(),
		today : "",
		days : [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
		pannel : '<div style="width:287px;height:311px;text-align:center;font-size:12px;background:url(/styles/images/cal_bg.jpg) no-repeat;">'+
						'<div style="height:36px;padding:0px 6px;font-size:16px;position:relative;">'+
							'<div id="monthPrev{time}" style="width:24px;height:24px;cursor:pointer;position:absolute;left:10px;top:6px;"></div>'+
							'<div style="width:98px;height:36px;margin:0px auto;">'+
								'<div id="year{time}" style="width:42px;height:36px;line-height:36px;float:left;font-weight:bold;color:#666;"></div>'+
								'<div style="width:16px;height:36px;line-height:36px;float:left;color:#666;">年</div>'+
								'<div id="month{time}" style="width:20px;height:36px;line-height:36px;float:left;font-weight:bold;color:#666;"></div>'+
								'<div style="width:16px;height:36px;line-height:36px;float:left;color:#666;">月</div>'+
							'</div>'+
							'<div id="monthNext{time}" style="width:24px;height:24px;cursor:pointer;position:absolute;right:10px;top:6px;"></div>'+
						'</div>'+
						'<div id="content{time}" style="height:275px;background:#fff;">'+
							'<table style="width:100%;height:100%;border-collapse:collapse;margin:0px;padding:0px;">'+
								'<tr style="height:29px;*height:26px;"><th style="border:1px solid #fff;">日</th><th style="border:1px solid #fff;">一</th><th style="border:1px solid #fff;">二</th><th style="border:1px solid #fff;">三</th><th style="border:1px solid #fff;">四</th><th style="border:1px solid #fff;">五</th><th style="border:1px solid #fff;">六</th></tr>'+
								'<tr style="height:41px;*height:39px;cursor:pointer;"><td style="border:1px solid #ddd;">&nbsp;</td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td></tr>'+
								'<tr style="height:41px;*height:39px;cursor:pointer;"><td style="border:1px solid #ddd;">&nbsp;</td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td></tr>'+
								'<tr style="height:41px;*height:39px;cursor:pointer;"><td style="border:1px solid #ddd;">&nbsp;</td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td></tr>'+
								'<tr style="height:41px;*height:39px;cursor:pointer;"><td style="border:1px solid #ddd;">&nbsp;</td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td></tr>'+
								'<tr style="height:41px;*height:39px;cursor:pointer;"><td style="border:1px solid #ddd;">&nbsp;</td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td></tr>'+
								'<tr style="height:41px;*height:39px;cursor:pointer;"><td style="border:1px solid #ddd;">&nbsp;</td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td><td style="border:1px solid #ddd;"></td></tr>'+
							'</table>'+
						'</div>'+
					 '</div>',
		setDays : function(temp){
			var year = temp.year,
				  month = temp.month,
				  yCell = temp.yCell,
				  mCell = temp.mCell,
				  cell = temp.cell,
				  day = new Date(year, month, 1).getDay();
			if(year % 100 == 0){
				if(year%400 == 0){
					temp.days[1] = 29;
				}else{
					temp.days[1] = 28;
				}
			}else{
				if(year % 4 == 0){
					temp.days[1] = 29;
				}else{
					temp.days[1] = 28;
				}
			};
			cell.html("&nbsp;");
			temp.cell&&temp.cell.css({"background":"none"});
			for(var i=0; i<temp.days[month]; i++){
				if((i + 1) == temp.now.getDate()){
					temp.today = cell.eq(day);
				}
				j = i + 1;
				cell.eq(day).html(j);
				var y = temp.year,
					  k = temp.month + 1,
					  m = k < 10 ? "0" +k : k,
					  d = j < 10 ? "0" + j : j,
					  data = "" + y + m + d;
				if(this.active[data]) {
					cell.eq(day).css({"background":"#ddd"});
				}
				day++;
			};
			yCell.html(year);
			mCell.html(month + 1);
			if(month == this.now.getMonth()) {
				temp.today&&temp.today.css({"background":"url(/styles/images/cur_bg.jpg)"});
			}
		},
		init : function(){
			var temp = this;
				  time =  this.now.getTime();
			this.div = $('<div id="ccontainer"></div>');
			this.div.css({"width": "287px", "position": "absolute", "zIndex": "100", "display": "none"});
			this.year = this.now.getFullYear();
			this.month = this.now.getMonth();
			this.div.html(this.pannel.replace(/\{time\}/g,time));
			this.container.append(this.div);
			this.yCell = $("#year" + time);
			this.mCell = $("#month" + time);
			this.cell = $("#content" + time + " td");
			this.setDays(temp);
			$("#monthPrev" + time).click(function(e){
				e.stopPropagation();
				temp.month--;
				if(temp.month < 0){
					temp.month = 11;
					temp.year--;
				}
				temp.setDays(temp);
			});
			$("#monthNext" + time).click(function(e){
				e.stopPropagation();
				temp.month++;
				if(temp.month > 11){
					temp.month = 0;
					temp.year++;
				}
				temp.setDays(temp);
			});
			this.cell.click(function(e){
				e.stopPropagation();
				if(!$(this).html().match(/\d+/)) {
					return false;
				}
				var year = temp.year,
					  j = temp.month + 1,
					  month = j < 10 ? "0" +j : j,
					  day = $(this).html().length == 1 ? "0" + $(this).html() : $(this).html(),
					  data = "" + year + month + day;
				if($(this).attr("data") != "1") {
					$(this).css({"background":"#ddd"});
					$(this).attr("data", "1");
					temp.active[data] = 1;
				} else {
					$(this).css({"background":"#fff"});
					$(this).attr("data", "0");
					delete temp.active[data];
				}
				temp.fire("choose", {"year" : temp.year, "month" : temp.month + 1, "day" : $(this).html(), "status": $(this).attr("data"), "data": data});
			});
		},
		hide : function() {
			this.div.hide();
		},
		show : function() {
			this.div.show();
		},
		setPosition: function(o) {
			this.div.css({"top": o.top, "left": o.left});
		},
		on : function(event, fn) {
			var self = this, evt = self.events;
			evt[event] = evt[event] ? evt[event] : [];
			evt[event].push( fn );
		},
		un: function( event ) {
			var self = this, evt = self.events;
			if( typeof event == "undefined" ) {
				delete self.events;
				self.events = {};
			} else {
				delete evt[event];
			}
		},
		fire: function( event, data ) {
			var self = this, fns = self.events[event];
			if( typeof fns == "undefined" ) return;
			for( var i = 0; i < fns.length; i++ ) {
				fns[i].call(self, data);
			}
		}
	}
	window.Calendar = Calendar;
})();