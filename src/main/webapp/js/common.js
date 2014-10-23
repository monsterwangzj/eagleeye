function popLayer(ele, flag){
	var self = this;
	self.pan = ele;
	self.cover = $("<div></div>");
	self.eventList = {};
	$("body").append(self.cover);
	self.show = function(fn) {
		var _height = $(window).height(),
			_width = $(window).width(),
			_scrollTop = $("html").scrollTop() || $("body").scrollTop(),
			_scrollLeft = $("html").scrollLeft() || $("body").scrollLeft(),
			_top = (_height - ele.outerHeight())/2 + _scrollTop,
			_left = (_width - ele.outerWidth())/2 + _scrollLeft,
			_scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight,
			_scrollWidth = document.documentElement.scrollWidth || document.body.scrollWidth;
		self.pan.css({
			"position": "absolute",
			"zIndex": "10",
			"display": "block",
			"top": _top,
			"left": _left
		});
		self.cover.css({
			"position": "absolute",
			"zIndex": "auto",
			"display": "block",
			"top": "0px",
			"left": "0px",
			"zIndex": "9",
			"background": "#000",
			"opacity": "0.6",
			"width": _scrollWidth,
			"height": _scrollHeight
		});
		fn && fn.call(self, self);
		self.eventList.show &&  self.fire("show");
		return self;
	};
	self.hide = function (fn) {
		self.pan.hide();
		self.cover.hide();
		fn && fn.call(self, self);
		self.eventList.hide && self.fire("hide");
	};
	if(flag == 1) {
		self.ensure = function (fn) {
			self.pan.hide();
			self.cover.hide();
			fn && fn.call(self, self);
			self.eventList.ensure && self.fire("ensure");
			return self;
		};
		self.cancel = function (fn) {
			self.pan.hide();
			self.cover.hide();
			fn && fn.call(self, self);
			self.eventList.show && self.fire("cancel");
		};
	}
	if(flag == 2) {
		self.submit = function (fn) {
			self.pan.hide();
			self.cover.hide();
			fn && fn.call(self, self);
			self.eventList.submit && self.fire("submit");
			return self;
		};

	}
};
popLayer.prototype = {
	constrcutor: popLayer,
	on: function(key, fn) {
		var self = this, fns = self.eventList;
		fns[key] = fns[key] ? fns[key] : [];
		fns[key].push(fn);
	},
	un: function(key) {
		if(key) {
			delete this.eventList[key];
		} else {
			this.eventList = {};
		}
		return this;
	},
	fire: function(key) {
		var self = this, fns = self.eventList;
		fns[key] = fns[key] ? fns[key] : [];
		for(var i = 0; i < fns[key].length; i++) {
			fns[key][i].call(self, self);
		}
	}
};
;(function(W) {
	//分页类
	function chPage(url, con, tcon, length, map) {
		var self = this;
		self.eventList = {};
		self.url = url;
		self.container = $(con);
		self.turnCon = $(tcon);
		self.base ='<tr class="tit"><td></td><td>故障处理人</td><td>手机短信</td><td>邮件</td><td>盛大有你</td> </tr>';
		self.foot = '<tr><td colspan="6" class="ri pt pb"><input id="btnSubmit" type="button" value="提 交" class="button"></td></tr>';
		self.pan = [];
		self.pan.push('<tr data="{id}">');
		self.pan.push('	<td><span class="{chaName0}" data="0"></span></td>');
		self.pan.push('	<td>{name}</td>');
		self.pan.push('	<td><span class="{chaName1}" data="1"></span></td>');
		self.pan.push('	<td><span class="{chaName2}" data="2"></span></td>');
		self.pan.push('	<td><span class="{chaName3}" data="4"></span></td>');
		self.pan.push('</tr>');
		self.pan = self.pan.join("");
		self.curPage = 1;
		self.length = parseInt(length);
		self.map = map;
		self.template = function(str, obj){
			var s = str;
			for(var arr in obj){
				var reg = new RegExp("\{" + arr + "\}", "g");
				if(str.indexOf(arr) != -1){
					var sp = obj[arr];
					s = s.replace(reg, sp);
				};
			};
			return s;
		};
		self.setPage = function( t ) {
			self.container.html("");
			self.turnCon.html("");
			self.curPage = t;
			$.get(self.url,{"p": t},  function(results) {
				results = eval("(" + results + ")");
				var data = results.data.monitors,
					monitorMap = self.map,
					pageList = [],
					html = [];
				self.monitorMap = monitorMap;
				//内容填充
				each(data, function(n, o) {
					if(monitorMap[o.id] == 1) {
						o.chaName0 = "ch02";
						o.chaName1 = "ch02";
						o.chaName2 = "ch01";
						o.chaName3 = "ch01";
					} else if(monitorMap[o.id] == 2) {
						o.chaName0 = "ch02";
						o.chaName1 = "ch01";
						o.chaName2 = "ch02";
						o.chaName3 = "ch01";
					} else if(monitorMap[o.id] == 3) {
						o.chaName0 = "ch02";
						o.chaName1 = "ch02";
						o.chaName2 = "ch02";
						o.chaName3 = "ch01";
					} else if(monitorMap[o.id] == 4) {
						o.chaName0 = "ch02";
						o.chaName1 = "ch01";
						o.chaName2 = "ch01";
						o.chaName3 = "ch02";
					} else if(monitorMap[o.id] == 5) {
						o.chaName0 = "ch02";
						o.chaName1 = "ch02";
						o.chaName2 = "ch01";
						o.chaName3 = "ch02";
					} else if(monitorMap[o.id] == 6) {
						o.chaName0 = "ch02";
						o.chaName1 = "ch01";
						o.chaName2 = "ch02";
						o.chaName3 = "ch02";
					} else if(monitorMap[o.id] == 7) {
						o.chaName0 = "ch02";
						o.chaName1 = "ch02";
						o.chaName2 = "ch02";
						o.chaName3 = "ch02";
					} else {
						o.chaName0 = "ch01";
						o.chaName1 = "ch01";
						o.chaName2 = "ch01";
						o.chaName3 = "ch01";
					}
					html.push(self.template(self.pan, o));
				});
				self.container.html(self.base + html.join("") + self.foot);
				self.eventList.setPage && self.fire("setPage");
				//分页列表
				if(self.length < 2) {
					self.turnCon.html("");
					return;
				}
				pageList.push('<a class="other">首页</a>');
				pageList.push('<a class="other">上一页</a>');
				if(self.length <= 7) {
					for(var i = 1; i <= self.length; i++){
						pageList.push('<a>'+i+'</a>');
					}
				} else {
					if( t <= 5 ) {
						for(var i = 1; i < 7; i++){
							pageList.push('<a>'+i+'</a>');
						}
						pageList.push('<a>...</a>');
						pageList.push('<a>'+self.length+'</a>');
					} else if( t + 3 <= self.length ) {
						pageList.push('<a>1</a>');
						pageList.push('<a>...</a>');
						for(var i = t - 3; i < t + 2; i++){
							pageList.push('<a>'+i+'</a>');
						}
						pageList.push('<a>...</a>');
						pageList.push('<a>'+self.length+'</a>');
					} else {
						pageList.push('<a>1</a>');
						pageList.push('<a>...</a>');
						for(var i = t - 3; i <= self.length; i++){
							pageList.push('<a>'+i+'</a>');
						}
					}
				};
				pageList.push('<a class="other">下一页</a>');
				pageList.push('<a class="other">尾页</a>');
				self.turnCon.html(pageList.join(""));
				self.turnCon.find("a").each(function(n, el) {
					if($(el).html() == t) {
						$(el).addClass("on");
					}
				});
				self.turnCon.unbind("click").bind("click", function(e) {
					var target = $(e.target);
					if((target.html() == "首页" || target.html() == "上一页") && t == 1) {
						return false;
					}
					if((target.html() == "尾页" || target.html() == "下一页") && t == self.length) {
						return false;
					}
					if(target[0]==this) {
						return;
					}
					if(target.html() == "...") {
						return;
					}
					if(target.html() == "首页") {
						self.setPage(1);
					}
					if(target.html() == "上一页") {
						if(self.curPage > 1) {
							self.curPage--;
							self.setPage(self.curPage);
						}
					}
					if(target.html() == "尾页") {
						self.setPage(self.length);
					}
					if(target.html() == "下一页") {
						if(self.curPage < self.length) {
							self.curPage++;
							self.setPage(self.curPage);
						}
					}
					if(target.html().match(/\d+/)) {
						self.curPage = parseInt(target.html());
						self.setPage(self.curPage);
					}
					window.scrollTo(0, 0);
				});
			});
		};
		self.setPage(1);
	};
	chPage.prototype = {
			constrcutor: chPage,
			on: function(key, fn) {
				var self = this, fns = self.eventList;
				fns[key] = fns[key] ? fns[key] : [];
				fns[key].push(fn);
			},
			un: function(key) {
				if(key) {
					delete this.eventList[key];
				} else {
					this.eventList = {};
				}
				return this;
			},
			fire: function(key) {
				var self = this, fns = self.eventList;
				fns[key] = fns[key] ? fns[key] : [];
				for(var i = 0; i < fns[key].length; i++) {
					fns[key][i].call(self, self);
				}
			}
		};
	function each(arr, fn){
		for(var n = 0; n < arr.length; n++){
			fn.call(arr[n], n ,arr[n]);
		};
	};
	W.chPage = chPage;
})(window);

(function(W) {
	function select(con, options) {
		var self = this, options = options || {};
		self.eventList = {};
		self.config = {
		    panels:"div.open",
			trigers:"div.select",
			activeCls:"on",
			callback:null
		};
		self.selectedValue = "";
		$.extend( self.config, options);
		self.flag = true;
		self.container = $(con);
		self.panels=self.container.find( self.config.panels );
	    self.trigers=self.container.find( self.config.trigers );
		self.trigers.find("input").attr("readonly", "readonly");
		var n = self.panels.find("a").length < 14 ? self.panels.find("a").length : 14;
		self.panels.css({"height": n*20, "overflowY": "auto", "overflowX":"hidden"});
		self.panels.hide();
		self.trigers.click(function(e) {
			e.stopPropagation();
			if ( self.flag ) {
				self.flag = false;
				self.panels.show();
			} else if ( !self.flag ) {
				self.flag = true;
				self.panels.hide();
			}
			
		});
		self.panels.delegate("a", "click", function(e) {
			e.stopPropagation();
			var val = $(this).attr("data");
			val = val || "";
			self.panels.find("a").removeClass("on");
			$(this).addClass("on");
			self.trigers.find("input").eq(0).val($(this).html());
			self.trigers.find("input").eq(1).val(val);
			self.selectedValue = val;
			self.panels.hide();
			self.flag = true;
			self.eventList.change && self.fire("change");
		});
		$("body").click(function() {
			self.flag = true;
			self.panels.hide();
		});
	};
	select.prototype = {
		constrcutor: select,
		on: function(key, fn) {
			var self = this, fns = self.eventList;
			fns[key] = fns[key] ? fns[key] : [];
			fns[key].push(fn);
		},
		un: function(key) {
			if(key) {
				delete this.eventList[key];
			} else {
				this.eventList = {};
			}
			return this;
		},
		fire: function(key) {
			var self = this, fns = self.eventList;
			fns[key] = fns[key] ? fns[key] : [];
			for(var i = 0; i < fns[key].length; i++) {
				fns[key][i].call(self, self);
			}
		}
	};
	W.select = select;
})(window);


function CheckIpnDomain(ipnDomain) {
	var ipArray,j;
	var ip = ipnDomain;
	if(/[A-Za-z_-]/.test(ip)) {
		if(!/^([\w-]+\.)+((com)|(net)|(org)|(gov\.cn)|(info)|(cc)|(com\.cn)|(net\.cn)|(org\.cn)|(name)|(biz)|(tv)|(cn)|(la))$/.test(ip)){
			// alert("不是正确的域名");
			return false;
		}
	} else {
		ipArray = ip.split(".");
		j = ipArray.length;
		if(j!=4) {
			// alert("不是正确的IP");
			return false;
		}
		for(var k=0; k<4 ; k++) {
			console.log(ipArray[k]);
			if(ipArray[k].length==0 || ipArray[k]>255) {
				// alert("不是正确的IP");
				return false;
			}
		}
	}
	return true;
}
