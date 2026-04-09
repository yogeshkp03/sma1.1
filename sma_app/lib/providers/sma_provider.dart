import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../data/models/sma_preference_model.dart';
import '../data/models/menu_item_model.dart';
import '../data/repositories/sma_repository.dart';
import '../data/services/recommendation_service.dart' as rec;

class SmaProvider extends ChangeNotifier {
  final SmaRepository _repository = SmaRepository();
  final rec.RecommendationService _recommendationService =
      rec.RecommendationService();

  List<SmaPreference> _preferencesList = [];
  final List<RecommendationHistory> _history = [];
  bool _isLoading = false;
  String? _error;
  int? _userId;

  SmaPreference? get preferences =>
      _preferencesList.isNotEmpty ? _preferencesList.first : null;
  List<SmaPreference> get preferencesList => _preferencesList;
  List<RecommendationHistory> get history => _history;
  bool get isLoading => _isLoading;
  String? get error => _error;

  void setUserId(int userId) {
    _userId = userId;
    loadPreferences();
  }

  Future<void> loadPreferences({bool forceRefresh = false}) async {
    if (_userId == null) {
      final prefs = await SharedPreferences.getInstance();
      _userId = prefs.getInt('user_id');
    }

    if (_userId == null) return;

    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final prefs = await _repository.getActivePreferences(_userId!,
          forceRefresh: forceRefresh);

      if (prefs.isNotEmpty) {
        _preferencesList = prefs;
      } else {
        _preferencesList = [
          SmaPreference(
            userId: _userId!.toString(),
            isEnabled: true,
            mealType: MealType.breakfast,
            scheduledTime: '08:00:00',
            includeWeekends: false,
            dietType: DietType.none,
            cuisinePreferences: [],
            maxCalories: 600,
            minProtein: 30,
            maxCarbs: 80,
            maxFats: 30,
            minFiber: 10,
            maxFiber: 50,
            isActive: true,
          ),
          SmaPreference(
            userId: _userId!.toString(),
            isEnabled: true,
            mealType: MealType.lunch,
            scheduledTime: '13:00:00',
            includeWeekends: false,
            dietType: DietType.none,
            cuisinePreferences: [],
            maxCalories: 600,
            minProtein: 30,
            maxCarbs: 80,
            maxFats: 30,
            minFiber: 10,
            maxFiber: 50,
            isActive: true,
          ),
          SmaPreference(
            userId: _userId!.toString(),
            isEnabled: true,
            mealType: MealType.dinner,
            scheduledTime: '20:00:00',
            includeWeekends: false,
            dietType: DietType.none,
            cuisinePreferences: [],
            maxCalories: 600,
            minProtein: 30,
            maxCarbs: 80,
            maxFats: 30,
            minFiber: 10,
            maxFiber: 50,
            isActive: true,
          ),
        ];
      }

      try {
        _history.clear();
        _history.addAll(
            await _recommendationService.getRecommendationHistory(_userId!));
      } catch (e) {
        debugPrint('Error loading history: $e');
      }
    } catch (e) {
      _error = e.toString();
      _preferencesList = [
        SmaPreference(
          userId: _userId!.toString(),
          isEnabled: true,
          mealType: MealType.breakfast,
          scheduledTime: '08:00:00',
          includeWeekends: false,
          dietType: DietType.none,
          cuisinePreferences: [],
          maxCalories: 600,
          minProtein: 30,
          maxCarbs: 80,
          maxFats: 30,
          minFiber: 10,
          maxFiber: 50,
          isActive: true,
        ),
        SmaPreference(
          userId: _userId!.toString(),
          isEnabled: true,
          mealType: MealType.lunch,
          scheduledTime: '13:00:00',
          includeWeekends: false,
          dietType: DietType.none,
          cuisinePreferences: [],
          maxCalories: 600,
          minProtein: 30,
          maxCarbs: 80,
          maxFats: 30,
          minFiber: 10,
          maxFiber: 50,
          isActive: true,
        ),
        SmaPreference(
          userId: _userId!.toString(),
          isEnabled: true,
          mealType: MealType.dinner,
          scheduledTime: '20:00:00',
          includeWeekends: false,
          dietType: DietType.none,
          cuisinePreferences: [],
          maxCalories: 600,
          minProtein: 30,
          maxCarbs: 80,
          maxFats: 30,
          minFiber: 10,
          maxFiber: 50,
          isActive: true,
        ),
      ];
    }

    _isLoading = false;
    notifyListeners();
  }

  Future<void> savePreferences(SmaPreference preference) async {
    await savePreferencesWithTimes(
      preference,
      breakfastTime: '8:00 AM',
      lunchTime: '1:00 PM',
      dinnerTime: '8:00 PM',
    );
  }

  Future<void> savePreferencesWithTimes(
    SmaPreference preference, {
    required String breakfastTime,
    required String lunchTime,
    required String dinnerTime,
  }) async {
    if (_userId == null || _userId! <= 0) {
      _error = 'Session expired. Please log in to save preferences.';
      _isLoading = false;
      notifyListeners();
      return;
    }

    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      for (var existingPref in _preferencesList) {
        if (existingPref.id != null) {
          await _repository.deletePreference(_userId!, existingPref.id!);
        }
      }
      _preferencesList.clear();

      final basePreference = SmaPreference(
        userId: _userId!.toString(),
        isEnabled: preference.isEnabled,
        mealType: MealType.lunch,
        scheduledTime: lunchTime,
        includeWeekends: preference.includeWeekends,
        dietType: preference.dietType,
        cuisinePreferences: preference.cuisinePreferences,
        maxCalories: preference.maxCalories,
        minProtein: preference.minProtein,
        maxCarbs: preference.maxCarbs,
        maxFats: preference.maxFats,
        minFiber: preference.minFiber,
        maxFiber: preference.maxFiber,
        budgetLimit: preference.budgetLimit,
        isActive: preference.isEnabled,
      );

      final breakfastPref = basePreference.copyWith(
        mealType: MealType.breakfast,
        scheduledTime: breakfastTime,
      );
      final lunchPref = basePreference.copyWith(
        mealType: MealType.lunch,
        scheduledTime: lunchTime,
      );
      final dinnerPref = basePreference.copyWith(
        mealType: MealType.dinner,
        scheduledTime: dinnerTime,
      );

      debugPrint('Creating all meal preferences in parallel...');

      final results = await Future.wait([
        _repository.createPreference(_userId!, breakfastPref),
        _repository.createPreference(_userId!, lunchPref),
        _repository.createPreference(_userId!, dinnerPref),
      ]);

      _preferencesList = results;
      debugPrint(
          'All preferences saved. List size: ${_preferencesList.length}');
    } catch (e) {
      debugPrint('Error saving preferences: $e');
      String errorMessage = e.toString();

      if (errorMessage.contains('Unable to connect') ||
          errorMessage.contains('TimeoutException') ||
          errorMessage.contains('SocketException') ||
          errorMessage.contains('Connection')) {
        _error =
            'Cannot connect to server. Please check your internet connection.';
      } else {
        _error = errorMessage;
      }
    }

    _isLoading = false;
    notifyListeners();
  }

  Future<void> toggleSma(bool enabled) async {
    if (_preferencesList.isEmpty || _userId == null) return;

    try {
      for (var pref in _preferencesList) {
        if (pref.id != null) {
          await _repository.togglePreference(_userId!, pref.id!, enabled);
        }
      }

      _preferencesList = _preferencesList
          .map((p) => p.copyWith(isEnabled: enabled, isActive: enabled))
          .toList();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<List<MenuItem>> getRecommendations(MealType mealType) async {
    if (_userId == null) return [];

    try {
      final recommendations =
          await _recommendationService.getMealRecommendations(
        userId: _userId!,
        mealType: mealType,
      );
      return recommendations;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return [];
    }
  }

  Future<MenuItem?> getRecommendation(MealType mealType) async {
    if (_userId == null) return null;

    try {
      final recommendation = await _recommendationService.getMealRecommendation(
        userId: _userId!,
        mealType: mealType,
      );
      return recommendation;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return null;
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
