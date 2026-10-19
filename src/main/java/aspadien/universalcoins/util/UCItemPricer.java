package aspadien.universalcoins.util;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.init.ModItems;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

public class UCItemPricer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final UCItemPricer INSTANCE = new UCItemPricer();
   private static final Map<String, Integer> ucPriceMap = new HashMap<>();
   private static final Path configPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
   private final Random random = new Random();

  public static UCItemPricer getInstance() {
     return INSTANCE;
  }



  public void loadConfigs() {
     File folder = configPath.toFile();
     if (!folder.exists()) {
       folder.mkdirs();

       updateItems();
       updateEnchantments();
       updatePotions();

       loadDefaults();

       priceCoins();

       if (ServerLifecycleHooks.getCurrentServer() != null) {
         priceCraftedItems();
      }

       writePriceLists();
    } else {
      try {
         loadPriceLists();
       } catch (IOException e) {
         LOGGER.error("Universal Coins: Failed to load config files", e);
      }
    }
  }

  private void initializeDefaultPrices() {
     updateItems();
     updateEnchantments();
     updatePotions();
     priceCoins();

     if (ServerLifecycleHooks.getCurrentServer() != null) {
       priceCraftedItems();
    }
     writePriceLists();
  }

  private void loadPriceLists() throws IOException {
     File folder = configPath.toFile();
     File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
     if (files == null)
      return;
     for (File file : files) {
       try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
         while ((line = br.readLine()) != null) {
           if (line.startsWith("//") || line.startsWith("#") || !line.contains("="))
             continue;  String[] data = line.split("=");
          try {
             ucPriceMap.put(data[0], Integer.parseInt(data[1]));
           } catch (NumberFormatException e) {
             LOGGER.warn("Universal Coins: Invalid price in {}", file.getName());
          }
        }
      }
    }
  }

    // Вспомогательный метод для защиты от продажи рюкзаков, сундуков с NBT и других сложных предметов
    private boolean hasComplexNBT(ItemStack stack) {
        if (!stack.hasTag()) return false;

        CompoundNBT tag = stack.getTag();
        if (tag == null) return false;

        // Список "безопасных" тегов, которые не содержат внутри себя инвентари.
        // Если у предмета есть тег, которого нет в этом списке, его продажа блокируется.
        Set<String> safeKeys = new HashSet<>(Arrays.asList(
                "Damage",              // Прочность
                "Enchantments",        // Зачарования на оружии/броне
                "StoredEnchantments",  // Зачарования в книгах
                "display",             // Переименованные вещи
                "RepairCost",          // Штраф наковальни
                "Potion",              // Зелья
                "CustomPotionEffects", // Кастомные эффекты зелий
                "CustomPotionColor",   // Цвет зелий
                "HideFlags",           // Флаги отображения тултипа
                "AttributeModifiers",  // Модификаторы атрибутов
                "Unbreakable"          // Неразрушимость
        ));

        for (String key : tag.getAllKeys()) {
            // Если находим опасный тег (например, "BlockEntityTag" от сундуков
            // или "Items"/"Inventory" от рюкзаков модов) — блокируем
            if (!safeKeys.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public int getItemPrice(ItemStack stack) {
        if (stack.isEmpty()) return -1;

        // 1. Проверка на сложные данные: запрещаем продажу рюкзаков с вещами / сундуков с NBT
        if (hasComplexNBT(stack)) return -1;

        ResourceLocation registryName = stack.getItem().getRegistryName();
        if (registryName == null) return -1;

        String itemName = registryName.toString();
        int itemPrice = ucPriceMap.getOrDefault(itemName, -1);

        // Проверка по тегам
        if (itemPrice == -1) {
            for (ResourceLocation tagLoc : stack.getItem().getTags()) {
                String tagName = "tag:" + tagLoc.toString();
                if (ucPriceMap.containsKey(tagName)) {
                    itemPrice = ucPriceMap.get(tagName);
                    break;
                }
            }
        }

        // Если базовой цены нет - нет смысла считать дальше
        if (itemPrice == -1) return -1;

        // Проверка зачарований
        if (stack.isEnchanted()) {
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                ResourceLocation enchReg = entry.getKey().getRegistryName();
                if (enchReg != null) {
                    String enchKey = enchReg + "." + entry.getValue();
                    int enchPrice = ucPriceMap.getOrDefault(enchKey, -1);
                    if (enchPrice != -1) {
                        itemPrice += enchPrice;
                    }
                }
            }
        }

        // Проверка зелий
        if (hasPotion(stack)) {
            ResourceLocation potionReg = PotionUtils.getPotion(stack).getRegistryName();
            if (potionReg != null) {
                String potionName = potionReg.toString();
                int potionPrice = ucPriceMap.getOrDefault(potionName, -1);
                if (potionPrice != -1) {
                    itemPrice += potionPrice;
                }
            }
        }

        // Учет прочности (Износ снижает цену)
        if (stack.isDamageableItem() && stack.getDamageValue() > 0) {
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamageValue(); // Сколько прочности потрачено

            if (maxDamage > 0) {
                // Вычисляем процент оставшейся "жизни" предмета
                double remainingPercent = (double)(maxDamage - currentDamage) / maxDamage;

                // В Minecraft при починке крафтом дается бонус +5% прочности,
                // а на наковальне +12%. Строго линейный расчет позволяет дюпать монеты.
                // Чтобы избежать этого, "сломанный" предмет сохраняет базовую ценность 15% (как материалы/рукоять),
                // а остальные 85% стоимости масштабируются пропорционально оставшейся прочности.
                double baseValue = 0.15;
                double durabilityValue = 0.85 * remainingPercent;

                // Умножаем цену на получившийся коэффициент
                itemPrice = (int) Math.round(itemPrice * (baseValue + durabilityValue));

                // Сломанный предмет не может стоить 0, если у него была цена, минимум 1 монета
                if (itemPrice < 1) itemPrice = 1;
            }
        }

        return itemPrice;
    }

    public void setItemPrice(ItemStack stack, int price) {
        if (stack.isEmpty() || price <= 0) return;
        String itemName = Objects.requireNonNull(stack.getItem().getRegistryName()).toString();
        ucPriceMap.put(itemName, price);
    }

    private void updateItems() {
        for (Item item : ForgeRegistries.ITEMS) {
            String name = Objects.requireNonNull(item.getRegistryName()).toString();
            if (!ucPriceMap.containsKey(name)) {
                ucPriceMap.put(name, -1);
            }
        }
    }

  private void updateEnchantments() {
     for (Enchantment ench : ForgeRegistries.ENCHANTMENTS) {
       for (int i = 1; i <= ench.getMaxLevel(); i++) {
         ucPriceMap.put(Objects.requireNonNull(ench.getRegistryName()) + "." + i, -1);
      }
    }
  }

  private void updatePotions() {
     ForgeRegistries.POTION_TYPES.getKeys().forEach(loc -> ucPriceMap.put(loc.toString(), -1));
  }



  private void loadDefaults() {
      String resourcePath = "pricelists/minecraft.csv";
      InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);

     if (stream == null) {
       LOGGER.error("Universal Coins: Could not find default pricelist at {}", resourcePath);

      return;
    }
     try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      String line;
       while ((line = reader.readLine()) != null) {
         if (line.startsWith("//") || line.startsWith("#") || !line.contains("="))
           continue;  String[] data = line.split("=");
         if (data.length == 2) {
           ucPriceMap.put(data[0], Integer.parseInt(data[1]));
        }
      }
       LOGGER.info("Universal Coins: Loaded base prices from resources.");
     } catch (IOException|NumberFormatException e) {
       LOGGER.error("Universal Coins: Error reading default pricelist", e);
    }
  }


  private void priceCoins() {
     ucPriceMap.put(Objects.requireNonNull(ModItems.IRON_COIN.get().getRegistryName()).toString(), UniversalCoins.coinValues[0]);
     ucPriceMap.put(Objects.requireNonNull(ModItems.GOLD_COIN.get().getRegistryName()).toString(), UniversalCoins.coinValues[1]);
     ucPriceMap.put(Objects.requireNonNull(ModItems.EMERALD_COIN.get().getRegistryName()).toString(), UniversalCoins.coinValues[2]);
     ucPriceMap.put(Objects.requireNonNull(ModItems.DIAMOND_COIN.get().getRegistryName()).toString(), UniversalCoins.coinValues[3]);
     ucPriceMap.put(Objects.requireNonNull(ModItems.OBSIDIAN_COIN.get().getRegistryName()).toString(), UniversalCoins.coinValues[4]);
     ucPriceMap.put((Objects.requireNonNull(ModItems.NETHERITE_COIN.get().getRegistryName())).toString(), UniversalCoins.coinValues[5]);
  }

    public void priceCraftedItems() {
        if (ServerLifecycleHooks.getCurrentServer() == null)
            return;
        LOGGER.info("Universal Coins: Starting Comprehensive Recipe Analysis...");

        net.minecraft.item.crafting.RecipeManager rm = ServerLifecycleHooks.getCurrentServer().getRecipeManager();

        // Собираем все поддерживаемые типы крафтов в один список
        List<IRecipe<?>> recipes = new ArrayList<>();
        recipes.addAll(rm.getAllRecipesFor(IRecipeType.CRAFTING));          // Верстак и инвентарь
        recipes.addAll(rm.getAllRecipesFor(IRecipeType.STONECUTTING));      // Камнерез
        recipes.addAll(rm.getAllRecipesFor(IRecipeType.SMELTING));          // Обычная печь
        recipes.addAll(rm.getAllRecipesFor(IRecipeType.BLASTING));          // Плавильная печь
        recipes.addAll(rm.getAllRecipesFor(IRecipeType.SMOKING));           // Коптильня
        recipes.addAll(rm.getAllRecipesFor(IRecipeType.CAMPFIRE_COOKING));  // Костер

        // Храним предметы, которым мы сгенерировали цену в этом сеансе.
        // Это нужно, чтобы позволить алгоритму перезаписывать сгенерированную цену,
        // если он найдет более дешевый способ крафта, но при этом НЕ трогать
        // ручные (базовые) цены, загруженные из ваших конфигов.
        Set<String> autoPriced = new HashSet<>();

        boolean changed;
        int passes = 0;

        do {
            changed = false;
            for (IRecipe<?> recipe : recipes) {
                ItemStack output = recipe.getResultItem();
                if (output.isEmpty())
                    continue;

                String outputName = Objects.requireNonNull(output.getItem().getRegistryName()).toString();
                int currentPrice = ucPriceMap.getOrDefault(outputName, -1);

                // Если у предмета УЖЕ есть цена, и она была задана не нами в этом цикле (т.е. загружена из конфига),
                // мы не пересчитываем ее через крафт. Она является "базовой".
                if (currentPrice != -1 && !autoPriced.contains(outputName)) {
                    continue;
                }

                int totalCost = 0;
                boolean canPrice = true;

                // Вычисляем стоимость ингредиентов
                for (Ingredient ing : recipe.getIngredients()) {
                    if (ing.isEmpty())
                        continue;

                    int bestPrice = -1;

                    // Проверяем все возможные предметы, подходящие под этот ингредиент (например, любую доску)
                    // и берем самый ДЕШЕВЫЙ вариант
                    for (ItemStack match : ing.getItems()) {
                        int p = getItemPrice(match);
                        if (p != -1 && (bestPrice == -1 || p < bestPrice)) {
                            bestPrice = p;
                        }
                    }

                    if (bestPrice == -1) {
                        canPrice = false;
                        break;
                    }
                    totalCost += bestPrice;
                }

                if (canPrice && totalCost > 0) {
                    // Если это рецепт печи, добавляем "налог" на топливо и работу
                    IRecipeType<?> type = recipe.getType();
                    if (type == IRecipeType.SMELTING || type == IRecipeType.BLASTING ||
                            type == IRecipeType.SMOKING || type == IRecipeType.CAMPFIRE_COOKING) {
                        totalCost += 2;
                    }

                    // Рассчитываем итоговую цену за 1 штуку
                    int finalPrice = totalCost / Math.max(1, output.getCount());

                    if (finalPrice > 0) {
                        // Если цены не было вообще, ИЛИ мы нашли более дешевый способ крафта (а цена генерировалась нами)
                        if (currentPrice == -1 || finalPrice < currentPrice) {
                            setItemPrice(output, finalPrice);
                            autoPriced.add(outputName);
                            changed = true;
                        }
                    }
                }
            }
            passes++;
        } while (changed && passes < 50);

        LOGGER.info("Universal Coins: Recipe Analysis finished in {} passes.", passes);
    }

  private void writePriceLists() {
    try {
      if (!Files.exists(configPath)) Files.createDirectories(configPath, (FileAttribute<?>[])new FileAttribute[0]);
      File csvFile = configPath.resolve("minecraft.csv").toFile();
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
       for (Map.Entry<String, Integer> entry : ucPriceMap.entrySet()) {
          writer.write(entry.getKey() + "=" + entry.getValue());
          writer.newLine();
        }
      }
    } catch (IOException e) {
      LOGGER.error("Failed to write pricelists", e);
    }
  }

  public boolean hasEnchantment(ItemStack stack) {
    return stack.isEnchanted();
  }

  public boolean hasPotion(ItemStack stack) {
    return !Objects.requireNonNull(PotionUtils.getPotion(stack).getRegistryName()).getPath().equals("empty");
  }

    public ItemStack getRandomPricedStack() {
        List<String> itemKeys = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : ucPriceMap.entrySet()) {
            String key = entry.getKey();
            int price = entry.getValue();

            // Зачарования содержат точку в ключе (например, sharpness.1).
            // Предметы точек не содержат. Исключаем теги.
            if (price > 0 && !key.contains(".") && !key.startsWith("tag:")) {
                itemKeys.add(key);
            }
        }

        if (itemKeys.isEmpty()) {
            return ItemStack.EMPTY;
        }

        String randomKey = itemKeys.get(this.random.nextInt(itemKeys.size()));

        try {
            ResourceLocation resLoc = new ResourceLocation(randomKey);
            Item item = ForgeRegistries.ITEMS.getValue(resLoc);

            if (item != null && item != Items.AIR) {
                return new ItemStack(item);
            }
        } catch (Exception e) {
            LOGGER.error("Universal Coins: Failed to create random ItemStack for key: {}", randomKey);
        }

        return ItemStack.EMPTY;
    }
}
