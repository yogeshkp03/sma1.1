import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../core/constants/app_colors.dart';
import '../../../data/models/sma_preference_model.dart';
import '../../../data/models/menu_item_model.dart';
import '../../../data/models/restaurant_model.dart';
import '../../../providers/sma_provider.dart';
import '../../../providers/cart_provider.dart';
import '../../widgets/image_placeholder.dart';
import '../cart/cart_screen.dart';

class SmaHomeScreen extends StatefulWidget {
  const SmaHomeScreen({super.key});

  @override
  State<SmaHomeScreen> createState() => _SmaHomeScreenState();
}

class _SmaHomeScreenState extends State<SmaHomeScreen> {
  List<MenuItem> _recommendations = [];
  int _selectedRecommendationIndex = 0;
  bool _isLoadingRecommendation = false;
  MealType? _upcomingMeal;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<SmaProvider>().loadPreferences();
    });
    _determineUpcomingMeal();
  }

  void _determineUpcomingMeal() {
    final now = TimeOfDay.now();
    final hour = now.hour;

    setState(() {
      if (hour >= 7 && hour < 11) {
        _upcomingMeal = MealType.breakfast;
      } else if (hour >= 11 && hour < 15) {
        _upcomingMeal = MealType.lunch;
      } else if (hour >= 15 && hour < 18) {
        _upcomingMeal = MealType.snacks;
      } else if (hour >= 18 && hour < 22) {
        _upcomingMeal = MealType.dinner;
      } else {
        _upcomingMeal = MealType.breakfast;
      }
    });
  }

  String get _upcomingMealDisplayName {
    switch (_upcomingMeal) {
      case MealType.breakfast:
        return 'Breakfast';
      case MealType.lunch:
        return 'Lunch';
      case MealType.dinner:
        return 'Dinner';
      case MealType.snacks:
        return 'Snacks';
      default:
        return 'Meal';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Smart Meal Autopilot'),
        actions: [
          IconButton(
            icon: const Icon(Icons.history),
            onPressed: () => _showRecommendationHistory(context),
          ),
        ],
      ),
      body: Consumer<SmaProvider>(
        builder: (context, smaProvider, child) {
          if (smaProvider.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          final prefs = smaProvider.preferences;
          if (prefs == null) {
            return _buildSetupPrompt(context);
          }

          return _buildDashboard(context, smaProvider);
        },
      ),
    );
  }

  Widget _buildSetupPrompt(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: AppColors.primary.withValues(alpha: 0.1),
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.auto_awesome,
                  size: 64, color: AppColors.primary),
            ),
            const SizedBox(height: 24),
            const Text(
              'Set Up Your\nSmart Meal Autopilot',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            Text(
              'Let AI find the perfect meals for you based on your preferences.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey[600], fontSize: 16),
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: () {
                Navigator.push(context,
                    MaterialPageRoute(builder: (_) => const SmaSetupScreen()));
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primary,
                foregroundColor: Colors.white,
                padding:
                    const EdgeInsets.symmetric(horizontal: 48, vertical: 16),
              ),
              child: const Text('Set Up Now',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDashboard(BuildContext context, SmaProvider smaProvider) {
    final prefsList = smaProvider.preferencesList;
    if (prefsList.isEmpty) {
      return _buildSetupPrompt(context);
    }

    final firstPref = prefsList.first;
    final allPrefs = prefsList;

    return RefreshIndicator(
      onRefresh: () => smaProvider.loadPreferences(),
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _SmaStatusCard(isActive: firstPref.isEnabled),
          const SizedBox(height: 16),
          _GetSuggestionsCard(
            upcomingMealName: _upcomingMealDisplayName,
            onGetSuggestions: () => _getSuggestions(context, smaProvider),
            isLoading: _isLoadingRecommendation,
            recommendations: _recommendations,
            selectedIndex: _selectedRecommendationIndex,
            onSelectRecommendation: (index) {
              setState(() => _selectedRecommendationIndex = index);
            },
            onAddToCart: _recommendations.isNotEmpty &&
                    _selectedRecommendationIndex < _recommendations.length
                ? () => _addToCart(
                    context, _recommendations[_selectedRecommendationIndex])
                : null,
            onClearRecommendation: () => setState(() => _recommendations = []),
          ),
          const SizedBox(height: 16),
          _ScheduleCard(preferencesList: allPrefs),
          const SizedBox(height: 16),
          _NutritionGoalsCard(preferences: firstPref),
          const SizedBox(height: 24),
          Consumer<SmaProvider>(
            builder: (context, smaProvider, _) {
              final hasPreferences = smaProvider.preferencesList.isNotEmpty &&
                  smaProvider.preferencesList.any((p) => p.id != null);
              return ElevatedButton.icon(
                onPressed: () => Navigator.push(context,
                    MaterialPageRoute(builder: (_) => const SmaSetupScreen())),
                icon: Icon(hasPreferences ? Icons.edit : Icons.add),
                label: Text(
                    hasPreferences ? 'Edit Preferences' : 'Set Preferences'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primary,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
              );
            },
          ),
        ],
      ),
    );
  }

  Future<void> _getSuggestions(
      BuildContext context, SmaProvider smaProvider) async {
    if (_upcomingMeal == null) return;
    final scaffoldMessenger = ScaffoldMessenger.of(context);
    setState(() => _isLoadingRecommendation = true);
    try {
      final recommendations =
          await smaProvider.getRecommendations(_upcomingMeal!);
      setState(() {
        _recommendations = recommendations;
        _selectedRecommendationIndex = 0;
        _isLoadingRecommendation = false;
      });
      if (mounted && recommendations.isEmpty) {
        scaffoldMessenger.showSnackBar(const SnackBar(
            content: Text('No recommendations found'),
            backgroundColor: Colors.orange));
      }
    } catch (e) {
      setState(() => _isLoadingRecommendation = false);
      if (mounted) {
        scaffoldMessenger.showSnackBar(
            SnackBar(content: Text('Error: $e'), backgroundColor: Colors.red));
      }
    }
  }

  void _addToCart(BuildContext context, MenuItem item) {
    final cartProvider = Provider.of<CartProvider>(context, listen: false);

    // Create a temporary restaurant if none exists
    Restaurant restaurantToAdd;
    if (cartProvider.restaurant != null) {
      restaurantToAdd = cartProvider.restaurant!;
    } else {
      restaurantToAdd = Restaurant(
        id: item.restaurantId ?? 0,
        name: item.restaurantName ?? 'Restaurant',
        rating: 4.0,
        deliveryTimeMin: 30,
        deliveryTimeMax: 45,
        deliveryFee: 40,
        minOrder: 100,
      );
    }

    cartProvider.addItem(
      menuItem: item,
      restaurant: restaurantToAdd,
      quantity: 1,
    );

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${item.name} added to cart!'),
        backgroundColor: Colors.green,
        action: SnackBarAction(
          label: 'View Cart',
          textColor: Colors.white,
          onPressed: () => Navigator.push(
              context, MaterialPageRoute(builder: (_) => const CartScreen())),
        ),
      ),
    );
    setState(() => _recommendations = []);
  }

  void _showRecommendationHistory(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => DraggableScrollableSheet(
        initialChildSize: 0.7,
        minChildSize: 0.5,
        maxChildSize: 0.95,
        expand: false,
        builder: (_, scrollController) => Container(
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Recommendation History',
                      style:
                          TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  IconButton(
                      icon: const Icon(Icons.close),
                      onPressed: () => Navigator.pop(ctx)),
                ],
              ),
              const SizedBox(height: 16),
              Expanded(
                child: Consumer<SmaProvider>(
                  builder: (context, provider, _) {
                    if (provider.history.isEmpty) {
                      return const Center(
                          child: Text('No recommendations yet'));
                    }
                    return ListView.builder(
                      controller: scrollController,
                      itemCount: provider.history.length,
                      itemBuilder: (context, index) {
                        final item = provider.history[index];
                        return Card(
                          margin: const EdgeInsets.only(bottom: 8),
                          child: ListTile(
                            leading: CircleAvatar(
                              backgroundColor:
                                  AppColors.primary.withValues(alpha: 0.1),
                              child: const Icon(Icons.auto_awesome,
                                  color: AppColors.primary),
                            ),
                            title: Text(item.menuItemName),
                            subtitle: Text(
                                '${item.mealType} • ${item.restaurantName}'),
                            trailing: Text('${item.calories ?? 0} cal'),
                          ),
                        );
                      },
                    );
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _SmaStatusCard extends StatelessWidget {
  final bool isActive;
  const _SmaStatusCard({required this.isActive});

  @override
  Widget build(BuildContext context) {
    return Card(
      color: isActive ? Colors.green[50] : Colors.grey[100],
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                  color: isActive ? Colors.green : Colors.grey,
                  shape: BoxShape.circle),
              child: Icon(isActive ? Icons.power : Icons.power_off,
                  color: Colors.white),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(isActive ? 'SMA Active' : 'SMA Paused',
                      style: const TextStyle(
                          fontWeight: FontWeight.bold, fontSize: 16)),
                  Text(
                      isActive
                          ? 'Meals auto-added 1 hour before scheduled'
                          : 'Enable SMA for automatic recommendations',
                      style: TextStyle(color: Colors.grey[600], fontSize: 12)),
                ],
              ),
            ),
            Switch(
              value: isActive,
              onChanged: (value) =>
                  context.read<SmaProvider>().toggleSma(value),
              activeThumbColor: Colors.green,
            ),
          ],
        ),
      ),
    );
  }
}

class _GetSuggestionsCard extends StatelessWidget {
  final String upcomingMealName;
  final VoidCallback onGetSuggestions;
  final bool isLoading;
  final List<MenuItem> recommendations;
  final int selectedIndex;
  final Function(int) onSelectRecommendation;
  final VoidCallback? onAddToCart;
  final VoidCallback onClearRecommendation;

  const _GetSuggestionsCard({
    required this.upcomingMealName,
    required this.onGetSuggestions,
    required this.isLoading,
    required this.recommendations,
    required this.selectedIndex,
    required this.onSelectRecommendation,
    this.onAddToCart,
    required this.onClearRecommendation,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: AppColors.primary.withValues(alpha: 0.1),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.lightbulb, color: AppColors.primary),
                const SizedBox(width: 8),
                Text('Upcoming: $upcomingMealName',
                    style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                        color: AppColors.primary)),
              ],
            ),
            const SizedBox(height: 12),
            if (recommendations.isNotEmpty) ...[
              SizedBox(
                height: 200,
                child: PageView.builder(
                  itemCount: recommendations.length,
                  onPageChanged: onSelectRecommendation,
                  itemBuilder: (context, index) {
                    final recommendation = recommendations[index];
                    return Container(
                      margin: const EdgeInsets.symmetric(horizontal: 4),
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(12)),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              CachedImage(
                                url: recommendation.imageUrl,
                                width: 60,
                                height: 60,
                                placeholderIcon: Icons.restaurant,
                                borderRadius: BorderRadius.circular(8),
                                fit: BoxFit.cover,
                              ),
                              const SizedBox(width: 12),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(recommendation.name,
                                        style: const TextStyle(
                                            fontWeight: FontWeight.bold)),
                                    Text(recommendation.restaurantName ?? '',
                                        style: TextStyle(
                                            color: Colors.grey[600],
                                            fontSize: 12)),
                                    const SizedBox(height: 4),
                                    Row(
                                      children: [
                                        _InfoChip(
                                            icon: Icons.local_fire_department,
                                            label:
                                                '${recommendation.calories} cal'),
                                        const SizedBox(width: 8),
                                        _InfoChip(
                                            icon: Icons.currency_rupee,
                                            label:
                                                '₹${recommendation.price.toStringAsFixed(0)}'),
                                      ],
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                          const Spacer(),
                          SizedBox(
                            width: double.infinity,
                            child: ElevatedButton.icon(
                              onPressed: onAddToCart,
                              icon:
                                  const Icon(Icons.add_shopping_cart, size: 18),
                              label: const Text('Add to Cart'),
                              style: ElevatedButton.styleFrom(
                                  backgroundColor: AppColors.primary,
                                  foregroundColor: Colors.white),
                            ),
                          ),
                        ],
                      ),
                    );
                  },
                ),
              ),
              if (recommendations.length > 1) ...[
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: List.generate(recommendations.length, (index) {
                    return Container(
                      margin: const EdgeInsets.symmetric(horizontal: 4),
                      width: 8,
                      height: 8,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: index == selectedIndex
                            ? AppColors.primary
                            : Colors.grey[300],
                      ),
                    );
                  }),
                ),
              ],
              const SizedBox(height: 8),
              TextButton.icon(
                onPressed: onClearRecommendation,
                icon: const Icon(Icons.refresh, size: 16),
                label: const Text('Get Different Suggestions'),
              ),
            ] else ...[
              Text('Get AI-powered meal suggestions based on your preferences',
                  style: TextStyle(color: Colors.grey[600], fontSize: 14)),
              const SizedBox(height: 12),
              ElevatedButton.icon(
                onPressed: isLoading ? null : onGetSuggestions,
                icon: isLoading
                    ? const SizedBox(
                        width: 16,
                        height: 16,
                        child: CircularProgressIndicator(
                            strokeWidth: 2, color: Colors.white))
                    : const Icon(Icons.auto_awesome),
                label: Text(isLoading
                    ? 'Getting Suggestions...'
                    : 'Get Suggestions Now'),
                style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primary,
                    foregroundColor: Colors.white),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _InfoChip extends StatelessWidget {
  final IconData icon;
  final String label;
  const _InfoChip({required this.icon, required this.label});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
          color: Colors.grey[100], borderRadius: BorderRadius.circular(12)),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 12, color: Colors.grey[600]),
          const SizedBox(width: 4),
          Text(label, style: TextStyle(fontSize: 11, color: Colors.grey[600])),
        ],
      ),
    );
  }
}

class _ScheduleCard extends StatelessWidget {
  final List<SmaPreference> preferencesList;
  const _ScheduleCard({required this.preferencesList});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.schedule, color: AppColors.primary),
                const SizedBox(width: 8),
                const Text('Meal Schedule',
                    style:
                        TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                const Spacer(),
                if (preferencesList.isNotEmpty &&
                    preferencesList.first.includeWeekends)
                  Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                        color: AppColors.primary.withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(12)),
                    child: const Text('Weekends On',
                        style: TextStyle(
                            fontSize: 10,
                            color: AppColors.primary,
                            fontWeight: FontWeight.bold)),
                  ),
              ],
            ),
            const SizedBox(height: 16),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: preferencesList.map((pref) {
                return _MealChip(
                    mealType: pref.mealType, time: pref.formattedTime);
              }).toList(),
            ),
          ],
        ),
      ),
    );
  }
}

class _MealChip extends StatelessWidget {
  final MealType mealType;
  final String time;
  const _MealChip({required this.mealType, required this.time});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: AppColors.primary.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: AppColors.primary.withValues(alpha: 0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(mealType.icon, size: 16, color: AppColors.primary),
          const SizedBox(width: 8),
          Text(mealType.displayName,
              style: const TextStyle(
                  color: AppColors.primary, fontWeight: FontWeight.w500)),
          const SizedBox(width: 8),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: BoxDecoration(
                color: AppColors.primary,
                borderRadius: BorderRadius.circular(8)),
            child: Text(time,
                style: const TextStyle(
                    color: Colors.white,
                    fontSize: 10,
                    fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }
}

class _NutritionGoalsCard extends StatelessWidget {
  final SmaPreference preferences;
  const _NutritionGoalsCard({required this.preferences});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Row(children: [
              Icon(Icons.pie_chart, color: AppColors.primary),
              SizedBox(width: 8),
              Text('Nutrition Goals',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16))
            ]),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                    child: _NutritionItem(
                        label: 'Calories',
                        value: '${preferences.maxCalories} cal',
                        color: Colors.orange,
                        icon: Icons.local_fire_department)),
                Expanded(
                    child: _NutritionItem(
                        label: 'Protein',
                        value: '${preferences.minProtein}g',
                        color: Colors.red,
                        icon: Icons.fitness_center)),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                    child: _NutritionItem(
                        label: 'Carbs',
                        value: '${preferences.maxCarbs}g',
                        color: Colors.blue,
                        icon: Icons.grain)),
                Expanded(
                    child: _NutritionItem(
                        label: 'Fats',
                        value: '${preferences.maxFats}g',
                        color: Colors.yellow[700]!,
                        icon: Icons.water_drop)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _NutritionItem extends StatelessWidget {
  final String label, value;
  final Color color;
  final IconData icon;
  const _NutritionItem(
      {required this.label,
      required this.value,
      required this.color,
      required this.icon});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      margin: const EdgeInsets.symmetric(horizontal: 4),
      decoration: BoxDecoration(
          color: color.withValues(alpha: 0.1),
          borderRadius: BorderRadius.circular(8)),
      child: Column(
        children: [
          Icon(icon, color: color),
          const SizedBox(height: 4),
          Text(value,
              style: TextStyle(fontWeight: FontWeight.bold, color: color)),
          Text(label, style: TextStyle(fontSize: 12, color: Colors.grey[600])),
        ],
      ),
    );
  }
}

class SmaSetupScreen extends StatefulWidget {
  const SmaSetupScreen({super.key});
  @override
  State<SmaSetupScreen> createState() => _SmaSetupScreenState();
}

class _SmaSetupScreenState extends State<SmaSetupScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _smaEnabled = true;
  bool _includeWeekends = false;
  int _maxCalories = 600;
  int _minProtein = 30;
  int _maxCarbs = 80;
  int _maxFats = 30;
  DietType _dietType = DietType.none;
  int? _budgetLimit;
  final Map<MealType, TimeOfDay> _mealTimes = {
    MealType.breakfast: const TimeOfDay(hour: 8, minute: 0),
    MealType.lunch: const TimeOfDay(hour: 13, minute: 0),
    MealType.dinner: const TimeOfDay(hour: 20, minute: 0),
  };

  @override
  void initState() {
    super.initState();
    final smaProvider = context.read<SmaProvider>();
    final prefsList = smaProvider.preferencesList;
    final prefs = smaProvider.preferences;

    if (prefs != null) {
      setState(() {
        _smaEnabled = prefs.isEnabled;
        _includeWeekends = prefs.includeWeekends;
        _maxCalories = prefs.maxCalories;
        _minProtein = prefs.minProtein;
        _maxCarbs = prefs.maxCarbs;
        _maxFats = prefs.maxFats;
        _dietType = prefs.dietType;
        _budgetLimit = prefs.budgetLimit;

        // Load meal times from all saved preferences
        for (final p in prefsList) {
          _mealTimes[p.mealType] = _parseTime(p.scheduledTime);
        }
      });
    }
  }

  TimeOfDay _parseTime(String time) {
    final parts = time.split(':');
    int hour = int.parse(parts[0]);
    final isPM = time.toUpperCase().contains('PM') && hour < 12;
    if (isPM) hour += 12;
    return TimeOfDay(hour: hour % 24, minute: 0);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('SMA Setup')),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            _buildSection('Enable Smart Meal Autopilot'),
            SwitchListTile(
              title: const Text('Enable SMA'),
              subtitle: const Text('Auto-add meals 1 hour before scheduled'),
              value: _smaEnabled,
              onChanged: (v) => setState(() => _smaEnabled = v),
            ),
            const SizedBox(height: 24),
            _buildSection('Meal Schedule'),
            ...MealType.values
                .where((m) => m != MealType.snacks)
                .map((m) => _buildMealTile(m)),
            SwitchListTile(
              title: const Text('Include Weekends'),
              value: _includeWeekends,
              onChanged: (v) => setState(() => _includeWeekends = v),
            ),
            const SizedBox(height: 24),
            _buildSection('Nutrition Goals'),
            _buildSlider('Max Calories', _maxCalories.toDouble(), 300, 1000,
                'cal', (v) => setState(() => _maxCalories = v.round())),
            _buildSlider('Min Protein', _minProtein.toDouble(), 10, 100, 'g',
                (v) => setState(() => _minProtein = v.round())),
            _buildSlider('Max Carbs', _maxCarbs.toDouble(), 20, 200, 'g',
                (v) => setState(() => _maxCarbs = v.round())),
            _buildSlider('Max Fats', _maxFats.toDouble(), 10, 80, 'g',
                (v) => setState(() => _maxFats = v.round())),
            const SizedBox(height: 24),
            _buildSection('Dietary Preferences'),
            DropdownButtonFormField<DietType>(
              initialValue: _dietType,
              decoration: const InputDecoration(
                  border: OutlineInputBorder(), labelText: 'Diet Type'),
              items: DietType.values
                  .map((t) =>
                      DropdownMenuItem(value: t, child: Text(t.displayName)))
                  .toList(),
              onChanged: (v) => setState(() => _dietType = v ?? DietType.none),
            ),
            const SizedBox(height: 16),
            TextFormField(
              initialValue: _budgetLimit?.toString(),
              decoration: const InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Budget Limit (₹)',
                  prefixText: '₹'),
              keyboardType: TextInputType.number,
              onChanged: (v) => _budgetLimit = int.tryParse(v),
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: _savePreferences,
              style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primary,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16)),
              child: const Text('Save Preferences',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSection(String title) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Text(title,
            style: const TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 16,
                color: AppColors.primary)),
      );

  Widget _buildMealTile(MealType mealType) {
    final time = _mealTimes[mealType] ?? const TimeOfDay(hour: 12, minute: 0);
    return ListTile(
      leading: Icon(mealType.icon, color: AppColors.primary),
      title: Text(mealType.displayName),
      subtitle: Text(_formatTime(time)),
      trailing: const Icon(Icons.chevron_right),
      onTap: () async {
        final picked =
            await showTimePicker(context: context, initialTime: time);
        if (picked != null) setState(() => _mealTimes[mealType] = picked);
      },
    );
  }

  String _formatTime(TimeOfDay t) {
    final h = t.hour.toString().padLeft(2, '0');
    final m = t.minute.toString().padLeft(2, '0');
    return '$h:$m:00';
  }

  Widget _buildSlider(String label, double value, double min, double max,
          String unit, Function(double) onChanged) =>
      Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            Text(label),
            Text('$value $unit',
                style: const TextStyle(fontWeight: FontWeight.bold))
          ]),
          Slider(
              value: value,
              min: min,
              max: max,
              activeColor: AppColors.primary,
              onChanged: onChanged),
        ],
      );

  void _savePreferences() async {
    final smaProvider = context.read<SmaProvider>();

    // Get user's selected times for each meal type
    final breakfastTime = _formatTime(
        _mealTimes[MealType.breakfast] ?? const TimeOfDay(hour: 8, minute: 0));
    final lunchTime = _formatTime(
        _mealTimes[MealType.lunch] ?? const TimeOfDay(hour: 13, minute: 0));
    final dinnerTime = _formatTime(
        _mealTimes[MealType.dinner] ?? const TimeOfDay(hour: 20, minute: 0));

    final preference = SmaPreference(
      id: smaProvider.preferences?.id,
      userId: smaProvider.preferences?.userId ?? '',
      isEnabled: _smaEnabled,
      mealType: MealType.lunch, // Primary meal type (doesn't matter for saving)
      scheduledTime: lunchTime,
      includeWeekends: _includeWeekends,
      dietType: _dietType,
      cuisinePreferences: [],
      maxCalories: _maxCalories,
      minProtein: _minProtein,
      maxCarbs: _maxCarbs,
      maxFats: _maxFats,
      minFiber: 10,
      maxFiber: 50,
      budgetLimit: _budgetLimit,
      isActive: _smaEnabled,
    );

    // Pass the meal times to the provider
    await smaProvider.savePreferencesWithTimes(
      preference,
      breakfastTime: breakfastTime,
      lunchTime: lunchTime,
      dinnerTime: dinnerTime,
    );

    if (mounted) {
      if (smaProvider.error != null) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
            content: Text(smaProvider.error!),
            backgroundColor: Colors.red,
            duration: const Duration(seconds: 4)));
        smaProvider.clearError();
      } else {
        // Reload preferences from backend to ensure consistency
        // Force refresh to bypass cache
        await smaProvider.loadPreferences(forceRefresh: true);
        if (mounted) {
          Navigator.pop(context); // Go back to dashboard
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
              content: Text('Preferences saved successfully!'),
              backgroundColor: Colors.green));
        }
      }
    }
  }
}
