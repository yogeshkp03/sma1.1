import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/cart_provider.dart';
import '../../providers/navigation_provider.dart';
import 'home/home_screen.dart';
import 'sma/sma_home_screen.dart';
import 'cart/cart_screen.dart';
import 'profile/profile_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  final List<Widget> _screens = [
    const HomeScreen(),
    const SmaHomeScreen(),
    const CartScreen(),
    const ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Consumer<NavigationProvider>(
        builder: (context, navProvider, _) {
          return IndexedStack(index: navProvider.currentTabIndex, children: _screens);
        },
      ),
      bottomNavigationBar: Consumer2<CartProvider, NavigationProvider>(
        builder: (context, cartProvider, navProvider, child) {
          return NavigationBar(
            selectedIndex: navProvider.currentTabIndex,
            onDestinationSelected: (index) {
              navProvider.setTabIndex(index);
            },
            destinations: [
              const NavigationDestination(
                icon: Icon(Icons.home_outlined),
                selectedIcon: Icon(Icons.home),
                label: 'Home',
              ),
              const NavigationDestination(
                icon: Icon(Icons.auto_awesome_outlined),
                selectedIcon: Icon(Icons.auto_awesome),
                label: 'SMA',
              ),
              NavigationDestination(
                icon: Badge(
                  isLabelVisible: cartProvider.itemCount > 0,
                  label: Text('${cartProvider.itemCount}'),
                  child: const Icon(Icons.shopping_cart_outlined),
                ),
                selectedIcon: Badge(
                  isLabelVisible: cartProvider.itemCount > 0,
                  label: Text('${cartProvider.itemCount}'),
                  child: const Icon(Icons.shopping_cart),
                ),
                label: 'Cart',
              ),
              const NavigationDestination(
                icon: Icon(Icons.person_outline),
                selectedIcon: Icon(Icons.person),
                label: 'Profile',
              ),
            ],
          );
        },
      ),
    );
  }
}
