myApp.controller('messageCtrl', ['$rootScope', '$scope', '$http', function($rootScope, $scope, $http) {
    $http.get('/ws/message/inbox/count/' + $rootScope.session.id).
    success(function(response) {
        $scope.inboxCount = response;
    });
    
    $http.get('/ws/message/sent/count/' + $rootScope.session.id).
    success(function(response) {
        $scope.sentCount = response;
    });
}]);

myApp.controller('inboxCtrl', ['$rootScope', '$scope', '$http', '$state', function($rootScope, $scope, $http, $state) {
    $scope.prop = {selectAll: false};
    
    $http.get('/ws/message/inbox/' + $rootScope.session.id).
    success(function(response) {
        $scope.inbox = response;
        for (m in $scope.inbox) {
			$scope.inbox[m].date = new Date($scope.inbox[m].date);
            $scope.inbox[m]['selected'] = false;
			var dateA = $scope.inbox[m].date.getFullYear() + "-" + ('0' + ($scope.inbox[m].date.getMonth()+1)).slice(-2) + "-" + ('0' + $scope.inbox[m].date.getDate()).slice(-2);
			var dateB = ('0' + $scope.inbox[m].date.getHours()).slice(-2) + ":" + ('0' + $scope.inbox[m].date.getMinutes()).slice(-2) + ":" + ('0' + $scope.inbox[m].date.getSeconds()).slice(-2);
			$scope.inbox[m].date = dateA + " " + dateB;
		}
    });
    
    $scope.deleteSelected = function() {
        var tobeDeleted = [];
        for (m in $scope.inbox) {
           if ($scope.inbox[m].selected == true) {
               tobeDeleted.push($scope.inbox[m].id);
           }
        }
        var res = $http.post('/ws/message/inbox/delete', {ids: tobeDeleted});
        res.success(function(response) {
            if (response) 
                $state.go($state.current, {}, {reload: true});
        });
    };
    
    $scope.markRead = function() {
        var tobeMarked = [];
        for (m in $scope.inbox) {
            if ($scope.inbox[m].selected == true) {
                tobeMarked.push($scope.inbox[m].id);
            }
        }
        var res = $http.post('/ws/message/markRead', {ids: tobeMarked});
        res.success(function(response) {
            if (response) 
                $state.go($state.current, {}, {reload: true});
        });
    };
    
    $scope.markUnRead = function() {
        var tobeMarked = [];
        for (m in $scope.inbox) {
            if ($scope.inbox[m].selected == true) {
                tobeMarked.push($scope.inbox[m].id);
            }
        }
        var res = $http.post('/ws/message/markUnRead', {ids: tobeMarked});
        res.success(function(response) {
            if (response) 
                $state.go($state.current, {}, {reload: true});
        });
    };
    
    $scope.selectAll = function() {
        for (m in $scope.inbox)
             $scope.inbox[m]['selected'] = true;
    };
    
    $scope.deselectAll = function() {
        for (m in $scope.inbox)
            $scope.inbox[m]['selected'] = false;
    };
    
    $scope.viewMessage = function(message_id) {
      $state.go('app.message.view', {id: message_id});  
    };
}]);

myApp.controller('sentCtrl', ["$rootScope", '$scope', '$http', '$state', function($rootScope, $scope, $http, $state) {
    $scope.prop = {selectAll: false};
    
    var res = $http.get('/ws/message/sent/' + $rootScope.session.id);
    res.success(function(response) {
        $scope.sent = response;
        for (m in $scope.sent) {
            $scope.sent[m]['selected'] = false;
			$scope.sent[m].date = new Date($scope.sent[m].date);
			var dateA = $scope.sent[m].date.getFullYear() + "-" + ('0' + ($scope.sent[m].date.getMonth()+1)).slice(-2) + "-" + ('0' + $scope.sent[m].date.getDate()).slice(-2);
			var dateB = ('0' + $scope.sent[m].date.getHours()).slice(-2) + ":" + ('0' + $scope.sent[m].date.getMinutes()).slice(-2) + ":" + ('0' + $scope.sent[m].date.getSeconds()).slice(-2);
			$scope.sent[m].date = dateA + " " + dateB;
		}
    });
    
     $scope.deleteSelected = function() {
        var tobeDeleted = [];
        for (m in $scope.sent) {
           if ($scope.sent[m].selected == true) {
               tobeDeleted.push($scope.sent[m].id);
           }
        }
        var res = $http.post('/ws/message/sent/delete', {ids: tobeDeleted});
        res.success(function(response) {
            if (response)
                $state.go($state.current, {}, {reload: true});
        });
    };
    
    $scope.selectAll = function() {
        for (m in $scope.sent)
             $scope.sent[m]['selected'] = true;
    };
    
    $scope.deselectAll = function() {
        for (m in $scope.sent)
            $scope.sent[m]['selected'] = false;
    };
    
    $scope.viewMessage = function(message_id) {
      $state.go('app.message.view', {id: message_id});  
    };
}]);

myApp.controller('composeCtrl', ["$rootScope", '$scope', '$http', '$state', '$stateParams', function($rootScope, $scope, $http, $state, $stateParams) {
    $scope.compose = {text: "", username: $stateParams.to, title: ""};
    
    $scope.eraseText = function() {
        $scope.compose.text = ""; 
    };
    
    $scope.sendMessage = function() {
        $scope.errors = {textError: false, usernameError: false, titleError: false};
        if ($scope.compose.username === "")
            $scope.errors.usernameError = true;
        if ($scope.compose.text === "")
            $scope.errors.textError = true;
        if ($scope.compose.title === "")
            $scope.errors.titleError = true;
        var res = $http.post('/ws/user/getIDbyUsername', {username: $scope.compose.username});
        res.success(function(response) {
            if (response.id == 0 || response.id == $rootScope.session.id)
                $scope.errors.usernameError = true;
            if (!$scope.errors.usernameError && !$scope.errors.textError && !$scope.errors.titleError) {
                var res = $http.post('/ws/message/send', {text: $scope.compose.text, title: $scope.compose.title, from: $rootScope.session.id, to: response.id, date: new Date()});
                res.success(function(response) {
                    if (response)
                        $state.go('app.message.sent', {}, {reload: true});
                });
            }
        });
    }
}]);

myApp.controller('viewCtrl', ["$rootScope", '$scope', '$http', '$state', '$stateParams', function($rootScope, $scope, $http, $state, $stateParams) {
    var res = $http.get('/ws/message/view/' + $stateParams.id);
    res.success(function(response) {
        if (response.to === $rootScope.session.username) {
            $scope.message = {text: response.text, title: response.title, from: response.from, type: "inbox"};
            var res = $http.post('/ws/message/markRead', {ids: [parseInt($stateParams.id)]});
            res.success(function(response) {});
        }
        else if (response.from === $rootScope.session.username)
            $scope.message = {text: response.text, title: response.title, to: response.to, type: "sent"};
        else
            $state.go('app.welcome');
    });
    
    $scope.reply = function() {
        $state.go('app.message.compose', {to: $scope.message.from});
    };
    
    $scope.delete = function() {
        if ($scope.message.type === "inbox")
            var res = $http.post('/ws/message/inbox/delete', {ids: [parseInt($stateParams.id)]});
        else if ($scope.message.type === "sent")
            var res = $http.post('/ws/message/sent/delete', {ids: [parseInt($stateParams.id)]});
        res.success(function(response) {
            if ($scope.message.type === "inbox")
                $state.go('app.message.inbox', {}, {reload: true});
            else if ($scope.message.type === "sent")
                $state.go('app.message.sent', {}, {reload: true});
        });
    };
}]);