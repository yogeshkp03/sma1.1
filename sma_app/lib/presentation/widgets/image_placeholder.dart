import 'package:flutter/material.dart';
import '../../core/constants/app_colors.dart';

class ImagePlaceholder extends StatelessWidget {
  final double? width;
  final double? height;
  final IconData icon;
  final Color? backgroundColor;
  final BorderRadius? borderRadius;
  final BoxFit fit;

  const ImagePlaceholder({
    super.key,
    this.width,
    this.height,
    this.icon = Icons.restaurant,
    this.backgroundColor,
    this.borderRadius,
    this.fit = BoxFit.cover,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      height: height,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            (backgroundColor ?? AppColors.primary).withValues(alpha: 0.3),
            (backgroundColor ?? AppColors.primary).withValues(alpha: 0.1),
          ],
        ),
        borderRadius: borderRadius ?? BorderRadius.circular(8),
      ),
      child: Center(
        child: Icon(
          icon,
          size: (height ?? 100) * 0.4,
          color: backgroundColor ?? AppColors.primary,
        ),
      ),
    );
  }
}

class CachedImage extends StatelessWidget {
  final String? url;
  final double? width;
  final double? height;
  final IconData placeholderIcon;
  final IconData? errorIcon;
  final Color? backgroundColor;
  final BorderRadius? borderRadius;
  final BoxFit fit;

  const CachedImage({
    super.key,
    required this.url,
    this.width,
    this.height,
    this.placeholderIcon = Icons.restaurant,
    this.errorIcon,
    this.backgroundColor,
    this.borderRadius,
    this.fit = BoxFit.cover,
  });

  @override
  Widget build(BuildContext context) {
    if (url == null || url!.isEmpty) {
      return ImagePlaceholder(
        width: width,
        height: height,
        icon: placeholderIcon,
        backgroundColor: backgroundColor,
        borderRadius: borderRadius,
        fit: fit,
      );
    }

    return ClipRRect(
      borderRadius: borderRadius ?? BorderRadius.circular(8),
      child: Image.network(
        url!,
        width: width,
        height: height,
        fit: fit,
        loadingBuilder: (context, child, loadingProgress) {
          if (loadingProgress == null) return child;
          return ImagePlaceholder(
            width: width,
            height: height,
            icon: placeholderIcon,
            backgroundColor: backgroundColor,
            fit: fit,
          );
        },
        errorBuilder: (context, error, stackTrace) {
          return ImagePlaceholder(
            width: width,
            height: height,
            icon: errorIcon ?? Icons.broken_image,
            backgroundColor: backgroundColor,
            fit: fit,
          );
        },
      ),
    );
  }
}
