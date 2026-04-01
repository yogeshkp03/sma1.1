import 'package:flutter/material.dart';

class NavigationProvider extends ChangeNotifier {
  int _currentTabIndex = 0;

  int get currentTabIndex => _currentTabIndex;

  void setTabIndex(int index) {
    _currentTabIndex = index;
    notifyListeners();
  }

  void goToHome() {
    setTabIndex(0);
  }

  void goToSma() {
    setTabIndex(1);
  }

  void goToCart() {
    setTabIndex(2);
  }

  void goToProfile() {
    setTabIndex(3);
  }
}
