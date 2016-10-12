myApp.controller('displayAuctionCtrl', ['$rootScope', '$scope', '$state', '$stateParams', '$http', '$cookies', '$interval', function($rootScope, $scope, $state, $stateParams, $http, $cookies, $interval) {
    $scope.currentTab = "desc";
    $scope.hasEnded = false;
    $scope.bidAmount = "";
    $scope.bidConfirmMessage = false;
    $scope.XMLcreated = false;
    $scope.hasStarted = false;
	$scope.noBid = false;
    
    $scope.countDownFun = function() {
        var ends = new Date($scope.auction.ends);
        var today = new Date();
        var sec = 0, min = 0, hour = 0, day = 0;
		var str = "Ends in ";
        sec = ~~((ends - today)/1000);
        if (sec > 60) {
            min = ~~(sec / 60);
            sec = sec - 60 * min;
            if (min > 60) {
                hour = ~~(min / 60);
                min = min - 60 * hour;
            }
            if (hour > 24) {
                day = ~~(hour / 24);
                hour = hour - 24 * day;
            }
        }
		if (day > 0) {
			str = str + day + " days, ";
			str = str + hour + " hours, ";
			str = str + min + " minutes and ";
			str = str + sec + " seconds";
		}
		else {
			if (hour > 0) {
				str = str + hour + " hours, ";
				str = str + min + " minutes and ";
				str = str + sec + " seconds";
			}
			else {
				if (min > 0) {
					str = str + min + " minutes and ";
					str = str + sec + " seconds";
				}
				else {
					if (sec > 0)
						str = str + sec + " seconds";
					else
						str = "";
				}
			}
		}
        $scope.countDown = str;
        if ($scope.countDown === "") {
            $interval.cancel($rootScope.promise);
            $scope.hasEnded = true;
            $rootScope.promise = undefined;
        }
    };
    
    
    $http.get('/ws/auction/' + $stateParams.id).
    success(function(response) {
        $scope.auction = response;
        if ($scope.auction.id == 0) {
            $state.go("app.welcome");
        }
		
		$http.get('/ws/bid/forAuction/' + $stateParams.id).
		success(function(response) {
			$scope.currentBid = response;
			if ($scope.currentBid.amount == 0) 
				$scope.noBid = true;
			else {
                var today = new Date();
                var sec = 0, min = 0, hour = 0;
                sec = ~~((today - $scope.currentBid.bid_time)/1000);
                if (sec > 60) {
                    min = ~~(sec / 60);
                    sec = sec - 60 * min;
                    if (min > 60) {
                        hour = ~~(min / 60);
                        min = min - 60 * hour;
                    }
                }
                if (hour >= 24)
                    $scope.currentBid.bid_time = ~~(hour/24) + " days ago";
                else if (hour >= 1)
                    $scope.currentBid.bid_time = hour + " hours ago";
                else if (min >= 1)
                    $scope.currentBid.bid_time = min + " minutes ago";
                else if (sec >= 1)
                    $scope.currentBid.bid_time = sec + " seconds ago";
                else
                    $scope.currentBid.bid_time = "a moment ago";
            }
		});
		
        $rootScope.title = $scope.auction.name;
		$scope.coord = [parseFloat($scope.auction.latitude), parseFloat($scope.auction.longitude)];
        
        if ($scope.auction.currently == $scope.auction.buy_price)
            $scope.hasEnded = true;
        
        if (response.started != null) {
            var started = new Date(response.started);
            $scope.startDate = started.getFullYear() + "-" + ('0' + (started.getMonth()+1)).slice(-2) + "-" + ('0' + started.getDate()).slice(-2);
            $scope.startTime = ('0' + started.getHours()).slice(-2) + ":" + ('0' + started.getMinutes()).slice(-2) + ":" + ('0' + started.getSeconds()).slice(-2);
            $scope.hasStarted = true;
        }
        if ($scope.hasStarted == true && !$scope.auction.hasEnded) {
            if (new Date() > new Date($scope.auction.ends))
                $scope.hasEnded = true;
            else 
                $rootScope.promise = $interval($scope.countDownFun, 1000);
        }
    });
    
	$http.get('/ws/image/get/' + $stateParams.id).
    success(function(response) {
        $scope.imgA = response[0];
        $scope.imgB = response[1];
        if ($scope.imgA === "" && $scope.imgB !== "") {
            $scope.imgA = $scope.imgB;
            $scope.imgB = "";
        }
    });
    
    $scope.creatorProfile = function() {
        $state.go("app.profile", {id: $scope.auction.user_id});
    };
    
    $scope.bid = function() {
        $scope.bidMsg = "";
        if (isNaN($scope.bidAmount) || $scope.bidAmount.length == 0) {
            $scope.bidMsg = "Please type a valid amount of money";
            $scope.bidAmount = "";
        }
        else if (!$scope.noBid && !isNaN($scope.bidAmount) && parseFloat($scope.bidAmount) <= $scope.currentBid.amount) {
            $scope.bidMsg = "Please bid more than the current bid amount";
            $scope.bidAmount = "";
        }
		else if ($scope.noBid && !isNaN($scope.bidAmount) && parseFloat($scope.bidAmount) < $scope.auction.first_bid) {
			$scope.bidMsg = "Please bid more than the current bid amount";
            $scope.bidAmount = "";
		}
        else
            $scope.bidConfirmMessage = true;
    };
    
    $scope.confirmBid = function() {
        var toSent = {amount: $scope.bidAmount, bidder: $rootScope.session.id, auction: $scope.auction.id};
        $http.post('/ws/bid/add', toSent).
        success(function(response) {
            if (response == true) {
                $scope.bidAmount = "";
                $scope.bidConfirmMessage = false;
                $state.go($state.current, {id: $stateParams.id}, {reload: true});
            }
        });
    };
    
    $scope.cancelBid = function() {
        $scope.bidAmount = "";
        $scope.bidConfirmMessage = false;
    };
    
    $scope.bidderProfile = function(user_id) {
        $state.go("app.profile", {id: user_id});
    };
    
    $scope.buyNow = function() {
        var toSent = {amount: $scope.auction.buy_price, bidder: $rootScope.session.id, auction: $scope.auction.id};
        $http.post('/ws/bid/add', toSent).
        success(function(response) {
            if (response)
                $state.go($state.current, {id: $stateParams.id}, {reload: true});
        });
    };
    
    $scope.begin = function() {
        $http.get('/ws/auction/begin/' + $stateParams.id).
        success(function(response) {
            var started = new Date(response);
            $scope.startDate = started.getFullYear() + "-" + ('0' + (started.getMonth()+1)).slice(-2) + "-" + ('0' + started.getDate()).slice(-2);
            $scope.startTime = ('0' + started.getHours()).slice(-2) + ":" + ('0' + started.getMinutes()).slice(-2) + ":" + ('0' + started.getSeconds()).slice(-2);
            $scope.hasStarted = true;
            if (new Date() > new Date($scope.auction.ends))
                $scope.hasEnded = true;
            else
                $rootScope.promise = $interval($scope.countDownFun, 1000);
        });
    };
    
    $scope.edit = function() {
        $state.go('app.auction.edit', {id: $stateParams.id});
    };
    
    $scope.delete = function() {
        $http.get('/ws/image/delete/' + $stateParams.id).
        success(function(response) {
			$http.get('/ws/auction/delete/' + $stateParams.id).
			success(function(response) {
				$state.go('app.welcome');
    		});
		});
    };
    
    $scope.viewBids = function() {
        $state.go("app.auction.history", {id: $stateParams.id});
    };
    
}]);

myApp.controller('createAuctionCtrl', ['$rootScope', '$scope', '$state', '$stateParams', '$http', '$cookies', function($rootScope, $scope, $state, $stateParams, $http, $cookies) {
    $scope.option = "Create";
    $scope.zoomVal = 2;
	$scope.auction = {name: "", first_bid: "", description: "", country: "", location: ""};
	$scope.tempDate = {selectEYear: "", selectEMonth: "", selectEDay: "", selectEHour: "", selectEMinute: "", selectESecond: ""};
	$scope.coord = [0, 0];
	$scope.buy_price = {amount: ""};
    
	$scope.months = [{month: "Jan", number: 1}, {month: "Feb", number: 2}, {month: "Mar", number: 3}, {month: "Apr", number: 4}, {month: "May", number: 5}, {month: "Jun", number: 6}, {month: "Jul", number: 7}, {month: "Aug", number: 8}, {month: "Sep", number: 9}, {month: "Oct", number: 10}, {month: "Nov", number: 11}, {month: "Dec", number: 12}];
    
    $scope.$on('mapInitialized', function(evt, evtMap) {
        $scope.map = evtMap;
        $scope.marker = $scope.map.markers[0];
    });
   
    $scope.mapClick = function(event) {
        $scope.marker.setPosition(event.latLng); 
        $scope.marker.visible = "true";
        $scope.coord = [$scope.marker.getPosition().lat(), $scope.marker.getPosition().lng()];
    };
    
    $scope.images = {imgA : "", imgB: ""};
    
    $scope.categoryPath = "";
    $scope.categoryPathList = [];
    $scope.categoryPathList.push({name: "All", id: 1});
    var res = $http.get('/ws/category/parent/1');
    res.success(function(response) {
        $scope.categoryList = response;
    });
    
    $scope.findSubCat = function(name, id) {
        var pos = -1, i = 0;
        while (i < $scope.categoryPathList.length) {
            if ($scope.categoryPathList[i].id == id){
                pos = i;
                break;
            }
            i++;
        }
        if (pos == -1)
            $scope.categoryPathList.push({name, id});
        else {
            while ($scope.categoryPathList.length > pos + 1)
                $scope.categoryPathList.pop();
        }
        var res = $http.get('/ws/category/parent/' + id);
        res.success(function(response) {
            $scope.categoryList = response;
        });
    };
    
    $scope.loadImgA = function() {
        var f = document.getElementById("imgA");
        f.files[0],
        r = new FileReader();
        r.onloadend = function(e){
            $scope.images.imgA = e.target.result;
        }
        r.readAsDataURL(f.files[0]);
    };
    
    $scope.loadImgB = function() {
        var f = document.getElementById("imgB");
        f.files[0],
        r = new FileReader();
        r.onloadend = function(e){
            $scope.images.imgB = e.target.result;
        }
        r.readAsDataURL(f.files[0]);
    };
    
    $scope.clearImg = function(img) {
        if (img === "imgA") {
            $scope.images.imgA = "";
            $('#imgA').val('');
        }
        else {
            $scope.images.imgB = "";
            $('#imgB').val('');
        } 
    };
	
	$scope.submitAuction = function() {
		$scope.basicFieldsError = false; 
		$scope.databaseError = false;
		$scope.NumberError = false;
		$scope.NumberBPError = false;
		$scope.categoryError = false;
		$scope.futureDateError = false;
		
		if ($scope.categoryList.length != 0)
			$scope.categoryError = true;
		
		if (isNaN($scope.auction.first_bid))
			$scope.NumberError = true;
		if  (isNaN($scope.buy_price.amount))
			$scope.NumberBPError = true;
		
		for (var field in $scope.auction) {
			if ($scope.auction[field].length == 0) {
				$scope.basicFieldsError = true;
				break;
			}
		}
		
		for (var field in $scope.tempDate) {
			if ($scope.tempDate[field].length == 0) {
				$scope.basicFieldsError = true;
				break;
			}
		}
		
		$scope.checkDate = new Date(parseInt($scope.tempDate.selectEYear), parseInt($scope.tempDate.selectEMonth.number) - 1, parseInt($scope.tempDate.selectEDay), parseInt($scope.tempDate.selectEHour), parseInt($scope.tempDate.selectEMinute), parseInt($scope.tempDate.selectESecond), 0).getTime();
			
		if ($scope.checkDate <= (new Date().getTime()))
			$scope.futureDateError = true;
		
		if (!$scope.basicFieldsError && !$scope.NumberError && !$scope.NumberBPError && !$scope.futureDateError && !$scope.categoryError) {
            
            if ($scope.buy_price.amount.length == 0)
                $scope.buy_price.amount = 0;
			
			$scope.auction.longitude = $scope.coord[1];
			$scope.auction.latitude = $scope.coord[0];
			$scope.auction.user_id = $rootScope.session.id;
            $scope.auction.buy_price = $scope.buy_price.amount;
			$scope.auction.ends = $scope.checkDate;
			$scope.auction.categoryList = $scope.categoryPathList;
			
			$http.post('/ws/auction/create', $scope.auction).
			success(function(response) {
				if (response == -1) 
					$scope.databaseError = true;
				else {
                    $scope.auction.id = response;
                    $http.post('/ws/image/upload', {id: $scope.auction.id, imgA: $scope.images.imgA, imgB: $scope.images.imgB}).
                    success(function(response) {
                        $state.go('app.auction.display', {id: $scope.auction.id});
                    });
				}
			});
		}
	};
	
}]);

myApp.controller('editAuctionCtrl', ['$rootScope', '$scope', '$state', '$stateParams', '$http', '$cookies', function($rootScope, $scope, $state, $stateParams, $http, $cookies) {
    $scope.option = "Save";
    $scope.categoryList = [];
    $scope.buy_price = {amount: ""};
    $scope.images = {imgA : "", imgB: ""};
    
    $scope.months = [{month: "Jan", number: 1}, {month: "Feb", number: 2}, {month: "Mar", number: 3}, {month: "Apr", number: 4}, {month: "May", number: 5}, {month: "Jun", number: 6}, {month: "Jul", number: 7}, {month: "Aug", number: 8}, {month: "Sep", number: 9}, {month: "Oct", number: 10}, {month: "Nov", number: 11}, {month: "Dec", number: 12}];
	
    $http.get('/ws/auction/' + $stateParams.id).
    success(function(response) {
        $scope.auction = response;
		delete $scope.auction.started;
        if($scope.auction.user_id != $rootScope.session.id)
            $state.go('app.welcome');
        
        $scope.coord = [parseFloat($scope.auction.latitude), parseFloat($scope.auction.longitude)];
        $scope.$on('mapInitialized', function(evt, evtMap) {
            $scope.map = evtMap;
            $scope.marker = $scope.map.markers[0];
            if ($scope.coord[0] != 0 && $scope.coord[1] != 0) {
                $scope.map.setCenter(new google.maps.LatLng($scope.coord[0], $scope.coord[1]));
                $scope.zoomVal = 16;
                $scope.marker.setPosition(new google.maps.LatLng($scope.coord[0], $scope.coord[1]));
                $scope.marker.visible = "true";
            }
            else
                $scope.zoomVal = 2;
        });

        $scope.mapClick = function(event) {
            $scope.marker.setPosition(event.latLng);
            $scope.marker.visible = "true";
            $scope.coord = [$scope.marker.getPosition().lat(), $scope.marker.getPosition().lng()];
        };
        
        $scope.tempDate = {};
        var ends = new Date($scope.auction.ends);
        $scope.tempDate.selectEYear = ends.getFullYear();
        $scope.tempDate.selectEMonth = $scope.months[ends.getMonth()];
        $scope.tempDate.selectEDay = ends.getDate();
        $scope.tempDate.selectEHour = ends.getHours();
        $scope.tempDate.selectEMinute = ends.getMinutes();
        $scope.tempDate.selectESecond = ends.getSeconds();
        $scope.categoryPathList = $scope.auction.categories;
        $scope.buy_price.amount = $scope.auction.buy_price;
    });
    
    $scope.findSubCat = function(name, id) {
        var pos = -1, i = 0;
        while (i < $scope.categoryPathList.length) {
            if ($scope.categoryPathList[i].id == id){
                pos = i;
                break;
            }
            i++;
        }
        if (pos == -1)
            $scope.categoryPathList.push({name, id});
        else {
            while ($scope.categoryPathList.length > pos + 1)
                $scope.categoryPathList.pop();
        }
        $http.get('/ws/category/parent/' + id).
        success(function(response) {
            $scope.categoryList = response;
        });
    };
    
    $http.get('/ws/image/get/' + $stateParams.id).
    success(function(response) {
        $scope.images.imgA = response[0];
        $scope.images.imgB = response[1];
        if ($scope.images.imgB === "./img/auction_images/imgA0.jpg")
            $scope.images.imgB = "";
    });
    
    $scope.loadImgA = function() {
        var f = document.getElementById("imgA");
        f.files[0],
        r = new FileReader();
        r.onloadend = function(e){
            $scope.images.imgA = e.target.result;
        }
        r.readAsDataURL(f.files[0]);
    };
    
    $scope.loadImgB = function() {
        var f = document.getElementById("imgB");
        f.files[0],
        r = new FileReader();
        r.onloadend = function(e){
            $scope.images.imgB = e.target.result;
        }
        r.readAsDataURL(f.files[0]);
    };
    
    $scope.clearImg = function(img) {
        if (img === "imgA") {
            if ($scope.images.imgA.indexOf("./img/auction_images/") != -1) {
                $http.get('/ws/image/deleteOne/' + $stateParams.id + '/0').
                success(function(response) {});
            }
            $scope.images.imgA = "";
            $('#imgA').val('');
        }
        else {
            if ($scope.images.imgB.indexOf("./img/auction_images/") != -1) {
                $http.get('/ws/image/deleteOne/' + $stateParams.id + '/1').
                success(function(response) {});
            }
            $scope.images.imgB = "";
            $('#imgB').val('');
        } 
    };
    
    $scope.submitAuction = function() {
		$scope.basicFieldsError = false; 
		$scope.databaseError = false;
		$scope.NumberError = false;
		$scope.NumberBPError = false;
		$scope.categoryError = false;
		$scope.futureDateError = false;
		
		if ($scope.categoryList.length != 0)
			$scope.categoryError = true;
		
		if (isNaN($scope.auction.first_bid))
			$scope.NumberError = true;
		if  (isNaN($scope.buy_price.amount))
			$scope.NumberBPError = true;
		
		for (var field in $scope.auction) {
			if ($scope.auction[field].length == 0) {
				$scope.basicFieldsError = true;
				break;
			}
		}
		
		$scope.checkDate = new Date(parseInt($scope.tempDate.selectEYear), parseInt($scope.tempDate.selectEMonth.number) - 1, parseInt($scope.tempDate.selectEDay), parseInt($scope.tempDate.selectEHour), parseInt($scope.tempDate.selectEMinute), parseInt($scope.tempDate.selectESecond), 0).getTime();
			
		if ($scope.checkDate <= (new Date().getTime()))
			$scope.futureDateError = true;
		
		if (!$scope.basicFieldsError && !$scope.NumberError && !$scope.NumberBPError && !$scope.futureDateError && !$scope.categoryError) {
            
            if ($scope.buy_price.amount.length == 0)
                $scope.buy_price.amount = 0;
			
			$scope.auction.longitude = $scope.coord[1];
			$scope.auction.latitude = $scope.coord[0];
			$scope.auction.user_id = $rootScope.session.id;
            $scope.auction.buy_price = $scope.buy_price.amount;
			$scope.auction.ends = $scope.checkDate;
			$scope.auction.categoryList = $scope.categoryPathList;
            delete $scope.auction.categories;
            delete $scope.auction.creator;
            delete $scope.auction.currently;
			
            $http.post('/ws/auction/edit', $scope.auction).
			success(function(response) {
                if ($scope.images.imgA.indexOf("data:") != -1 || $scope.images.imgB.indexOf("data:") != -1) {
                    $http.post('/ws/image/edit', {id: $scope.auction.id, imgA: $scope.images.imgA, imgB: $scope.images.imgB}).
                    success(function(response) {
                        $state.go('app.auction.display', {id: $scope.auction.id});
                    });
                }
                else
                    $state.go('app.auction.display', {id: $scope.auction.id});
            });
		}
	};
    
}]);

myApp.filter('range', function() {
	return function(input, min, max) {
	min = parseInt(min); //Make string input int
	max = parseInt(max);
	for (var i=min; i<max; i++)
		input.push(i);
	return input;
	};
});

myApp.directive('customOnChange', function() {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      var onChangeHandler = scope.$eval(attrs.customOnChange);
      element.bind('change', onChangeHandler);
    }
  };
});

myApp.controller('manageAuctionCtrl', ['$rootScope', '$scope', '$state', '$stateParams', '$http', '$cookies', function($rootScope, $scope, $state, $stateParams, $http, $cookies) {
	$http.get('/ws/auction/getUserAuctions/' + $rootScope.session.id).
    success(function(response) {
        $scope.auctionList = response;
    });
	
    $scope.edit = function(auctionid) {
        $state.go('app.auction.edit', {id: auctionid});
    };
	
	$scope.delete = function(pos, auctionid) {
        $http.get('/ws/image/delete/' + auctionid).
        success(function(response) {
			$http.get('/ws/auction/delete/' + auctionid).
			success(function(response) {
				$scope.auctionList.splice(pos, 1);
    		});
		});
    };
    
}]);

myApp.controller('historyAuctionCtrl', ['$rootScope', '$scope', '$state', '$stateParams', '$http', '$cookies', function($rootScope, $scope, $state, $stateParams, $http, $cookies) {
    $http.get('/ws/auction/history/' + $stateParams.id)
    .success(function(response) {
        $scope.bidList = response;
        for (b in $scope.bidList) {
            $scope.bidList[b].bid_time = new Date($scope.bidList[b].bid_time);
            var dateA = $scope.bidList[b].bid_time.getFullYear() + "-" + ('0' + ($scope.bidList[b].bid_time.getMonth()+1)).slice(-2) + "-" + ('0' + $scope.bidList[b].bid_time.getDate()).slice(-2);
			var dateB = ('0' + $scope.bidList[b].bid_time.getHours()).slice(-2) + ":" + ('0' + $scope.bidList[b].bid_time.getMinutes()).slice(-2) + ":" + ('0' + $scope.bidList[b].bid_time.getSeconds()).slice(-2);
			$scope.bidList[b].bid_time = dateA + " " + dateB;
        }
    });
    
    $scope.bidderProfile = function(user_id) {
        $state.go("app.profile", {id: user_id});
    };
}]);