package com.amazonscale.config;

import com.amazonscale.category.entity.Category;
import com.amazonscale.category.repository.CategoryRepository;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            Category electronics = Category.builder()
                    .name("Electronics")
                    .description("Gadgets, audio, and personal devices")
                    .imageUrl("https://images.unsplash.com/photo-1498049860654-af1a5c57abf3?w=500")
                    .build();

            Category fashion = Category.builder()
                    .name("Fashion")
                    .description("Apparel, footwear, and accessories")
                    .imageUrl("https://images.unsplash.com/photo-1445205170230-053b83016050?w=500")
                    .build();

            Category home = Category.builder()
                    .name("Home & Kitchen")
                    .description("Furniture, appliances, and home decor")
                    .imageUrl("https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=500")
                    .build();

            categoryRepository.saveAll(List.of(electronics, fashion, home));
        }

        if (productRepository.count() == 0) {
            Category electronics = categoryRepository.findByName("Electronics").orElse(null);
            Category fashion = categoryRepository.findByName("Fashion").orElse(null);
            Category home = categoryRepository.findByName("Home & Kitchen").orElse(null);

            Product p1 = Product.builder()
                    .name("Wireless Noise-Cancelling Headphones")
                    .description("Immersive sound experience with active noise cancellation and 30-hour battery life.")
                    .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500")
                    .price(new BigDecimal("199.99"))
                    .originalPrice(new BigDecimal("249.99"))
                    .discountPercentage(new BigDecimal("20.00"))
                    .stock(45)
                    .brand("Sony")
                    .active(true)
                    .category(electronics)
                    .rating(new BigDecimal("4.8"))
                    .reviewCount(128)
                    .sku("SKU-AUDIO-001")
                    .slug("wireless-noise-cancelling-headphones")
                    .status("ACTIVE")
                    .featured(true)
                    .build();

            Product p2 = Product.builder()
                    .name("Smartphone Pro Max")
                    .description("Flagship mobile device with Super Retina display, 5G performance, and pro triple camera.")
                    .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500")
                    .price(new BigDecimal("999.99"))
                    .originalPrice(new BigDecimal("1099.99"))
                    .discountPercentage(new BigDecimal("9.00"))
                    .stock(20)
                    .brand("Apple")
                    .active(true)
                    .category(electronics)
                    .rating(new BigDecimal("4.9"))
                    .reviewCount(340)
                    .sku("SKU-PHONE-002")
                    .slug("smartphone-pro-max")
                    .status("ACTIVE")
                    .featured(true)
                    .build();

            Product p3 = Product.builder()
                    .name("Mechanical RGB Gaming Keyboard")
                    .description("Tactile mechanical switches with custom per-key RGB backlighting and aluminum frame.")
                    .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500")
                    .price(new BigDecimal("89.99"))
                    .originalPrice(new BigDecimal("119.99"))
                    .discountPercentage(new BigDecimal("25.00"))
                    .stock(60)
                    .brand("Logitech")
                    .active(true)
                    .category(electronics)
                    .rating(new BigDecimal("4.6"))
                    .reviewCount(85)
                    .sku("SKU-KB-003")
                    .slug("mechanical-rgb-gaming-keyboard")
                    .status("ACTIVE")
                    .featured(false)
                    .build();

            Product p4 = Product.builder()
                    .name("Ergonomic Running Shoes")
                    .description("Lightweight mesh breathability with responsive cushioning for marathon comfort.")
                    .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500")
                    .price(new BigDecimal("129.99"))
                    .originalPrice(new BigDecimal("149.99"))
                    .discountPercentage(new BigDecimal("13.00"))
                    .stock(30)
                    .brand("Nike")
                    .active(true)
                    .category(fashion)
                    .rating(new BigDecimal("4.7"))
                    .reviewCount(210)
                    .sku("SKU-SHOE-004")
                    .slug("ergonomic-running-shoes")
                    .status("ACTIVE")
                    .featured(true)
                    .build();

            Product p5 = Product.builder()
                    .name("Automatic Drip Coffee Maker")
                    .description("Programmable 12-cup glass carafe coffee maker with thermal warming plate.")
                    .imageUrl("https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500")
                    .price(new BigDecimal("49.99"))
                    .originalPrice(new BigDecimal("69.99"))
                    .discountPercentage(new BigDecimal("28.00"))
                    .stock(15)
                    .brand("Philips")
                    .active(true)
                    .category(home)
                    .rating(new BigDecimal("4.5"))
                    .reviewCount(94)
                    .sku("SKU-COFFEE-005")
                    .slug("automatic-drip-coffee-maker")
                    .status("ACTIVE")
                    .featured(false)
                    .build();

            productRepository.saveAll(List.of(p1, p2, p3, p4, p5));
        }
    }
}
