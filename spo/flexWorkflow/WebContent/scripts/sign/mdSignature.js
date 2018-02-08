if (!mdSignDataObject) {
	var mdSignDataObject = function() {
		var CRYPTO_ALGORITHM_RSA = 10001;
		var CRYPTO_ALGORITHM_VALIDATA = 10002;
		var ISSUER_DELIMITER = ";";

		var _this = null;

		var _localFileApi = null;
		var _base64Api = null;
		var _signDataApi = null;
		var _versionInfoApi = null;

		var _cipfs = null;
		var _issuers = null;
		var _signCert = null;

		var _signRequired = true;
		var _isLoginAlias = false;

		function _getLocalFileApi() {
			if (_localFileApi == null) {
				_localFileApi = new ActiveXObject("MDInterop.LocalFile");
			}
			return _localFileApi;
		}

		function _getSignDataApi() {
			if (_signDataApi == null) {
				_signDataApi = new ActiveXObject("MDInterop.SignData3");
				_setCipfs([CRYPTO_ALGORITHM_RSA,  CRYPTO_ALGORITHM_VALIDATA]);
			}
			return _signDataApi;
		}

		function _getVersionInfoApi() {
			if (_versionInfoApi == null) {
				_versionInfoApi = new ActiveXObject("MDInterop.VersionInfo");
			}
			return _versionInfoApi;
		}

		function _getBase64Api() {
			if (_base64Api == null) {
				_base64Api = new ActiveXObject("MDInterop.Base64");
			}
			return _base64Api;
		}

		function _convertArrayToByteArray(u8Array) {
			return _getBase64Api().normalizeJSArray(u8Array);
		}

		function _getContentByUrl(_url) {
			var xhr = new XMLHttpRequest();
			xhr.open('GET', _url, false);

			result = null;
			xhr.onreadystatechange = function() {
				if (xhr.readyState == 4) {
					if (xhr.status == 200) {
						result = xhr.responseBody;
					}
				}
			};
			xhr.send();

			return _convertArrayToByteArray(result);
		}

		function _getContentByUrlStr(_url) {
			var xhr = new XMLHttpRequest();
			xhr.open('GET', _url, false);

			result = null;
			xhr.onreadystatechange = function() {
				if (xhr.readyState == 4) {
					if (xhr.status == 200) {
						result = xhr.responseText;
					}
				}
			};

			xhr.send();

			return result;
		}

		function _myTrim(x) {
			return x.replace(/^\s+|\s+$/gm,'');
		}

		function _splitIssuersListWithDelimiter(s, d) {
			var list = s.split(d);
			var results = [];
			if (s != null && _myTrim(s) != "" && list != null && list.length > 0) {
				for (i = 0; i < list.length; i++) {
					results.push(_myTrim(list[i]));
				}
			}
			return results;
		}

		function _setCipfs(cipfs) {
			_getSignDataApi().clearCipfs();
			_cipfs = cipfs;
			for (var i = 0; i < cipfs.length; ++i) {
				_getSignDataApi().addCipf(cipfs[i]);
			}
		}

		function _setIssuers(issuers) {
			for (var i = 0; i < issuers.length; ++i) {
				_getSignDataApi().set_parameter(100, issuers[i]);
			}
			_issuers = issuers;
		}

		function _getErrorMaskDescription(mask) {
			if (mask == 0) {
				return "";
			}
			if (mask == -1) {
				return 'Ошибка СКЗИ';
			}
			result = [];
			if (mask & 1) {
				result.push('Неизвестная ошибка');
			}
			if (mask & 2) {
				result.push('Сертификат уже(еще) не валиден');
			}
			if (mask & 4) {
				result.push('Сертификат отозван');
			}
			if (mask & 8) {
				result.push('СОС недоступен');
			}
			if (mask & 16) {
				result.push('Сертификат издателя уже(еще) не валиден');
			}
			if (mask & 32) {
				result.push('Сертификат издателя отозван');
			}
			return result.join(',\n');
		}

		function _CCertShowCert() {
			var statusCode = _getSignDataApi().getStatusCode();
			if (_signCert != null && statusCode != null && statusCode == 0) {
				try {
					_signCert.showUI();
				} catch(err) {
					// TODO Обработана ошибка при закрытии диалога
				}
			} else {
				var msg = "";
				if (_signCert == null) {
					msg = "Подпись нарушена";
				} else {
					msg = "Ошибка проверки подписи"
				}
				msg = msg + "\n\nversion: '" + _getVersion() + "'";
				msg = msg + "\nПодробная информация (code='" + statusCode + "'):\n" + _getErrorMaskDescription(statusCode) + ".";
				var cipf = _getSignDataApi().getSignCipf();
				msg = msg + "\nСКЗИ, signCipf: " + cipf + "\n";
				var status = _getSignDataApi().getStatus();
				if (status != null && status.length > 0) {
					msg = msg + "\n\nstacktrace:\n"
					msg = msg + status;
				}
				alert(msg);
			}
		}

		function _getVersion() {
			var v_obj = _getVersionInfoApi();
			var v = v_obj.getVersion();
			return v + ".1";
		}

        function _certSign(content) {
            var result = null;
			if (!_isLoginAlias) {
				result = _getSignDataApi().signData(content);
				var statusCode = _getSignDataApi().getStatusCode();
				if (statusCode != 0) {
					var msg = "\nversion: '" + _getVersion() + "'";
					msg = msg + "\n\nПодробная информация (code='" + statusCode + "'):\n" + _getErrorMaskDescription(statusCode) + ".";
					var cipf = _getSignDataApi().getSignCipf();
					msg = msg + "\nСКЗИ, signCipf: " + cipf + "\n";
					var status = _getSignDataApi().getStatus();
					if (status != null && status.length > 0) {
						msg = msg + "\n\nstacktrace:\n";
						msg = msg + status;
					}
					alert(msg);
				}
			}
            return result;
        }

        function _getStrContent(content) {
			return _strToByteArray(content);
		}

        function _certVerifySign(sign, content) {
            if (sign == '') {
                alert('Подпись отсутствует');
                return false;
            }
            _signCert = _getSignDataApi().verifySign(content, sign);
            _CCertShowCert();
            return _signCert != null || 0 != _getSignDataApi().getStatusCode();
        }

        function _strToByteArray(str) {
			var uintAr=new ActiveXObject("Scripting.Dictionary");
			for(var i=0,j=str.length;i<j;++i){
				uintAr.add(i, str.charCodeAt(i));
			}
			return _getBase64Api().normalizeJSArray(uintAr.Items());
        }
		_this = {
			initSign: function(signRequired, isLoginAlias, cryptoIssuers) {
				try {
					_signRequired = (signRequired != null && signRequired) ? true : false;
					_isLoginAlias = (isLoginAlias != null && (isLoginAlias == 1 || isLoginAlias == true)) ? true : false;
					_signRequired = _signRequired && !_isLoginAlias;
					var issuers = _splitIssuersListWithDelimiter(cryptoIssuers, ';');
					_setIssuers(issuers);
				} catch(err) {
					alert("Ошибка при подписании '" + ((err.description == undefined) ? err : err.description) + "'");
					throw err;
				}
				try {
					_getSignDataApi();
				} catch(err) {
					if(_signRequired) {
						alert( "Невозможно подписать. Возможные причины:\n"
							+"1) Криптопровайдер не установлен\n"
							+"2) Рабочий сертификат не указан\n"
							+"3) Рабочий сертификат просрочен\n"
							+"(Подробная информация:"+ ((err.description == undefined) ? err : err.description) + ")" );
					} else {
						alert('На данном ПК не установлен/не работает криптопровайдер. Документ будет прикреплен без подписи.\n'
							+ '(Подробная информация: Не удалось создать объект MDInterop.SignData3. "' + ((err.description == undefined) ? err : err.description) + '")');
					}
					throw err;
				}
				return mdSignDataObject;
			},
			getSignRequired : function() {
				return _signRequired;
			},
			/////////////////////////////////////////////////////////////
            certSign: function (content) {
				var strContent = _getStrContent(content);
                return _certSign(strContent);
            },
            certSignURL: function (url) {
                var content = _getContentByUrl(url);
                return _certSign(content);
            },
            certSignFile: function (fileName) {
                var content = _getLocalFileApi().readFile(fileName);
                return _certSign(content);
            },
            /////////////////////////////////////////////////////////////
            certVerifySign: function (sign, content) {
				var strContent = _getStrContent(content);
                return _certVerifySign(sign, strContent);
            },
            certVerifySignURL: function (sign, url) {
                var content = _getContentByUrl(url);
                return _certVerifySign(sign, content);
            },
            certVerifySignFile: function (sign, fileName) {
                var content = _getLocalFileApi().readFile(fileName);
                return _certVerifySign(sign, content);
            },
			/////////////////////////////////////////////////////////////
			getVersion: function () {
				return _getVersion();
			},
			/////////////////////////////////////////////////////////////
			getIssuers: function () {
				return _issuers;
			},
			getCipfs: function () {
				return _cipfs;
			},
			onlyValidata: function () {
				_setCipfs([CRYPTO_ALGORITHM_VALIDATA]);
			},
			onlyRsa: function () {
				_setCipfs([CRYPTO_ALGORITHM_RSA]);
			},
			getSignCipf: function () {
				return _getSignDataApi().getSignCipf();
			},
			/////////////////////////////////////////////////////////////
			getStatus: function () {
				return _getSignDataApi().getStatus();
			},
			getStatusCode: function () {
				return _getSignDataApi().getStatusCode();
			},
            test: function() {
                return {
					sign: function (content) {
						return _certSign(content);
					},
					verifySign: function (sign, content) {
						return _certVerifySign(sign, content);
					},
                    getFileContent: function (fileName) {
                        return _getLocalFileApi().readFile(fileName);
                    },
                    getFileContentStr: function (fileName) {
                        return _getLocalFileApi().readFileStr(fileName);
                    },
                    getUrlContent: function (url) {
                        return _getContentByUrl(url);
                    },
                    getUrlContentStr: function (url) {
                        return _getContentByUrlStr(url);
                    },
                    getStrContent: function (content) {
                        return _getStrContent(content);
                    },
					showMyCert: function() {
						var cert = _getSignDataApi().getMyCert();
						if (cert != null) {
							cert.showUI();
						} else {
							alert("Suddenly, there is no my cert... it's a bit strange.");
						}
					}
                };
            }()
		};

		return _this;
	}();
}
