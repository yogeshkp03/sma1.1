import 'package:flutter/material.dart';
import '../data/models/restaurant_model.dart';
import '../data/models/menu_item_model.dart';
import '../data/repositories/restaurant_repository.dart';

class RestaurantProvider extends ChangeNotifier {
  final RestaurantRepository _repository = RestaurantRepository();

  List<Restaurant> _restaurants = [];
  List<String> _locations = [];
  List<MenuItem> _menuItems = [];
  Restaurant? _selectedRestaurant;
  bool _isLoading = false;
  String? _error;
  String? _selectedLocation;

  List<Restaurant> get restaurants => _restaurants;
  List<String> get locations => _locations;
  List<MenuItem> get menuItems => _menuItems;
  Restaurant? get selectedRestaurant => _selectedRestaurant;
  bool get isLoading => _isLoading;
  String? get error => _error;
  String? get selectedLocation => _selectedLocation;

  Future<void> loadRestaurants({String? location, String? search}) async {
    _isLoading = true;
    _error = null;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });

    try {
      _restaurants = await _repository.getRestaurants(
        location: location,
        search: search,
      );
      _isLoading = false;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    }
  }

  Future<void> loadLocations() async {
    try {
      _locations = await _repository.getLocations();
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    } catch (e) {
      _error = e.toString();
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    }
  }

  Future<void> loadMenu(int restaurantId) async {
    _isLoading = true;
    _error = null;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });

    try {
      _menuItems = await _repository.getMenuByRestaurant(restaurantId);
      _isLoading = false;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    }
  }

  Future<void> selectRestaurant(int restaurantId) async {
    _isLoading = true;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });

    try {
      _selectedRestaurant = await _repository.getRestaurantById(restaurantId);
      await loadMenu(restaurantId);
      _isLoading = false;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        notifyListeners();
      });
    }
  }

  void setLocation(String? location) {
    _selectedLocation = location;
    loadRestaurants(location: location);
  }

  void searchRestaurants(String query) {
    loadRestaurants(search: query, location: _selectedLocation);
  }

  void clearSelection() {
    _selectedRestaurant = null;
    _menuItems = [];
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  void clearError() {
    _error = null;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }
}
