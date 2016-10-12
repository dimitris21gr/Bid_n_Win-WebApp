myApp.controller('loginCtrl', ['$rootScope', '$scope', '$state', '$http', '$cookies', function($rootScope, $scope, $state, $http, $cookies) {
    $scope.user = {username: "", password: "", remember: false};
    $scope.tryLogin = function() {
        $scope.error = false;
        $scope.activationError = false;
        if ($scope.user.username.length == 0 || $scope.user.password.length == 0)  {
            $scope.error = true;
        }
        if ($scope.user.password.length < 5) {
            $scope.error = true;
        }
        if (!$scope.error) {
            var res = $http.post('/ws/user/login', $scope.user);
            res.success(function(response) {
                if (response.id != 0) {
                    if (response.activation) {
                        $rootScope.session = {username: $scope.user.username, id: response.id, isAdmin: response.superuser, loggedIn: true};
						$cookies.putObject('username', $rootScope.session.username);
						$cookies.putObject('loggedIn', $rootScope.session.loggedIn);
						$cookies.putObject('isAdmin', $rootScope.session.isAdmin);
						$cookies.putObject('id', $rootScope.session.id);
                        if (response.superuser)
                            $state.go('app.adminPage');
                        else
                            $state.go('app.welcome');
                    }
                    else
                        $scope.activationError = true;
                }
                else {
                    $scope.error = true;
                }
            });

        }
    };
}]);

myApp.controller('logoutCtrl', ['$rootScope', '$state', '$cookies', function($rootScope, $state, $cookies) {
    $rootScope.session = {username: "none", loggedIn: false, isAdmin: false, id: 0};
	$cookies.putObject('username', 'none');
	$cookies.putObject('loggedIn', false);
	$cookies.putObject('isAdmin', false);
	$cookies.putObject('id', 0);
    $state.go('app.welcome');
}]);

myApp.controller('registerCtrl', ['$scope', '$http', '$rootScope', '$state', function($scope, $http, $rootScope, $state) {
    $scope.user = {username: "", password: "", email: "", name: "", surname: "", location: "", telephone: "", address: "", country: "", trn: "" };
    $scope.prop = {accept: false, verifyPassword: "", verifyEmail: ""};
	$scope.tryRegister = function() {
		$scope.basicFieldsError = false; //these fields must be filled
		$scope.missmatchPassError = false;
		$scope.acceptTermsError = false;
		$scope.userExists = false;
        $scope.emailExists = false;
        $scope.passwordError = false;
		$scope.databaseError = false;
		$scope.registerComplete = false;
		$scope.missmatchEmailError = false;

		for (var field in $scope.user) {
			if ($scope.user[field].length == 0) {
				$scope.basicFieldsError = true;
				break;
			}
		}
        
        if (!$scope.basicFieldsError && $scope.user.password.length < 5) {
            $scope.passwordError = true;
        }
		else { 
			if ($scope.prop.verifyPassword !== $scope.user.password) 
				$scope.missmatchPassError = true;
			if ($scope.prop.verifyEmail !== $scope.user.email) 
				$scope.missmatchEmailError = true;
		}
		
		if (!$scope.prop.accept) 
			$scope.acceptTermsError = true;
		

		if (!$scope.acceptTermsError && !$scope.basicFieldsError && !$scope.missmatchPassError && !$scope.missmatchEmailError && !$scope.passwordError) {
			var res = $http.get('/ws/user/checkUsername/' + $scope.user.username);
			res.success(function(response) {
				if (response) {
                    $scope.userExists = true;
                }
				else {
					var res = $http.post('/ws/user/checkEmail', $scope.user);
					res.success(function(response) {
                        if (response) {
                            $scope.emailExists = true;
                        }
                        else {
                            var res = $http.post('/ws/user/register', $scope.user);
                            res.success(function(response) {
                                if (!response) 
                                    $scope.databaseError = true;
                                else 
                                    $scope.registerComplete = true;
                            });
                        }	
					});
				}
			});
		}
	}
}]);

myApp.controller('profileCtrl', ['$rootScope', '$scope', '$http', '$state', '$stateParams', function($rootScope, $scope, $http, $state, $stateParams) {
    $scope.user_id = parseInt($stateParams.id);
    
    if ($scope.user_id == 0) {   //user views his OWN profile
        $scope.user_id = $rootScope.session.id;
	}
    
    $http.get('/ws/user/getProfile/' + $scope.user_id).
    success(function(response) {
        $scope.user = response;
    });
    
    $scope.uploadAvatar = function() {
        $http.post('/ws/avatar/upload', {id: $scope.user_id, imgA: $scope.imgA}).
        success(function(response) {
            $state.go('app.profile', {id: $scope.user_id}, {reload: true});
        });
	};
	
	$scope.loadImgA = function() {
        var f = document.getElementById("imgA");
        f.files[0],
        r = new FileReader();
        r.onloadend = function(e){
            $scope.imgA = e.target.result;
            $scope.uploadAvatar();
        }
        r.readAsDataURL(f.files[0]);
    };	
    
    $scope.sendMsg = function() {
        $state.go("app.message.compose", {to: $scope.user.username});
    };

}]);

myApp.controller('editprofileCtrl', ['$rootScope', '$scope', '$http', '$stateParams', function($rootScope, $scope, $http, $stateParams) {
    $scope.user_id = parseInt($stateParams.id);
	$scope.currentUser = "";
	
	$scope.settings = {save: false, edit: true, isAdmin: false, showBanner: false};
    if ($scope.user_id == 0) {   //user views his OWN profile
        $scope.user_id = $rootScope.session.id;
		$scope.settings.isAdmin = true;
	}
   
    var res = $http.get('/ws/user/getProfile/' + $scope.user_id);
    res.success(function(response) {
        $scope.user = response;
		 $scope.currentUser = $scope.user.username;
    });
    
	$scope.editInfo = function() {
		console.log($scope.user);
		$scope.basicFieldsError = false;
		$scope.successfulUpdate = false;
		$scope.usernameExists = false; $scope.emailExists = false;
		
		if  (($scope.user.email.length == 0) || ($scope.user.name.length == 0) || ($scope.user.surname.length == 0)
			|| ($scope.user.username.length == 0)) {
				$scope.basicFieldsError = true;
        }
		
		if (!$scope.basicFieldsError) {
			var res = $http.post('/ws/user/getIDbyUsername', {username: $scope.currentUser});
        	res.success(function(response) {
				$scope.user.id = response.id;
				var res = $http.post('/ws/user/updateProfileInfo', $scope.user);
				res.success(function(response) {
					if (response == 1) {
						$scope.successfulUpdate = true;
						$scope.settings.edit = true;
					}
					else if (response == 2) {
						$scope.usernameExists = true;
					}
					else if (response == 3) {
						$scope.emailExists = true;
					}
				});
			});
		}
	};
	
    $scope.activateUser = function() {
        var res = $http.get('/ws/user/activate/' + $scope.user_id);
        res.success(function(response) {
            if (response)
                $scope.user.activation = true;
        });
    };
    
     $scope.banUser = function() {
        var res = $http.get('/ws/user/ban/' + $scope.user_id);
        res.success(function(response) {
            if (response)
                $scope.user.activation = false;
        });
    };
}]);