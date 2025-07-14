#!/usr/bin/env python3
"""
Comprehensive TSV Test Data Generator for POS Application
Generates various TSV files to test upload functionality including:
- Valid data
- Limit testing
- Error scenarios
- Null/Empty values
- Duplicate entries
- Column misalignment
"""

import csv
import os
import random
import string
from datetime import datetime

class TSVTestGenerator:
    def __init__(self, output_dir="test_tsvs"):
        self.output_dir = output_dir
        os.makedirs(output_dir, exist_ok=True)
        
        # Common test data
        self.client_names = ["TestClient1", "TestClient2", "TestClient3", "BigCorp", "SmallBiz"]
        self.product_names = ["Laptop", "Mouse", "Keyboard", "Monitor", "Tablet", "Phone", "Headphones", "Speaker", "Camera", "Printer"]
        self.urls = [
            "https://images.pexels.com/photos/1234567/pexels-photo-1234567.jpeg",
            "https://images.pexels.com/photos/7654321/pexels-photo-7654321.jpeg",
            "https://images.pexels.com/photos/9876543/pexels-photo-9876543.jpeg",
            ""  # Empty URL
        ]

    def generate_random_barcode(self, prefix="BC", length=10):
        """Generate a random barcode with given prefix"""
        suffix = ''.join(random.choices(string.ascii_uppercase + string.digits, k=length-len(prefix)))
        return f"{prefix}{suffix}"

    def generate_random_price(self, min_price=1.0, max_price=9999.99):
        """Generate a random price"""
        return round(random.uniform(min_price, max_price), 2)

    def write_tsv(self, filename, headers, rows):
        """Write TSV file with given headers and rows"""
        filepath = os.path.join(self.output_dir, filename)
        with open(filepath, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f, delimiter='\t')
            writer.writerow(headers)
            writer.writerows(rows)
        print(f"Generated: {filepath} ({len(rows)} rows)")

    def generate_valid_product_tsv(self, filename="valid_products.tsv", count=100):
        """Generate valid product TSV with specified count"""
        headers = ["barcode", "clientName", "name", "mrp", "imageUrl"]
        rows = []
        
        for i in range(count):
            row = [
                self.generate_random_barcode("VALID", 8),
                random.choice(self.client_names),
                f"{random.choice(self.product_names)} {i+1}",
                self.generate_random_price(),
                random.choice(self.urls)
            ]
            rows.append(row)
        
        self.write_tsv(filename, headers, rows)

    def generate_limit_test_product_tsv(self, filename="limit_test_products.tsv", count=5001):
        """Generate product TSV exceeding the 5000 row limit"""
        headers = ["barcode", "clientName", "name", "mrp", "imageUrl"]
        rows = []
        
        for i in range(count):
            row = [
                self.generate_random_barcode("LIMIT", 8),
                random.choice(self.client_names),
                f"Limit Test Product {i+1}",
                self.generate_random_price(),
                random.choice(self.urls)
            ]
            rows.append(row)
        
        self.write_tsv(filename, headers, rows)

    def generate_error_product_tsv(self, filename="error_products.tsv"):
        """Generate product TSV with various validation errors"""
        headers = ["barcode", "clientName", "name", "mrp", "imageUrl"]
        rows = [
            # Missing required fields
            ["", "TestClient1", "Product 1", 100.0, ""],
            ["BC001", "", "Product 2", 100.0, ""],
            ["BC002", "TestClient2", "", 100.0, ""],
            ["BC003", "TestClient3", "Product 3", "", ""],
            
            # Invalid MRP values
            ["BC004", "TestClient1", "Product 4", -10.0, ""],
            ["BC005", "TestClient2", "Product 5", 0.0, ""],
            ["BC006", "TestClient3", "Product 6", "not_a_number", ""],
            ["BC007", "TestClient1", "Product 7", "abc", ""],
            
            # Very long strings
            ["BC008", "TestClient2", "A" * 500, 100.0, ""],
            ["BC009", "X" * 500, "Product 9", 100.0, ""],
            
            # Special characters
            ["BC010", "Test@Client", "Product<>10", 100.0, ""],
            ["BC011", "Test\tClient", "Product\n11", 100.0, ""],
            
            # Valid row for comparison
            ["BC012", "TestClient1", "Valid Product", 99.99, "https://example.com/image.jpg"]
        ]
        
        self.write_tsv(filename, headers, rows)

    def generate_null_empty_product_tsv(self, filename="null_empty_products.tsv"):
        """Generate product TSV with null and empty values"""
        headers = ["barcode", "clientName", "name", "mrp", "imageUrl"]
        rows = [
            # Empty strings
            ["", "", "", "", ""],
            ["BC101", "", "", "", ""],
            ["", "TestClient1", "", "", ""],
            
            # Whitespace only
            [" ", " ", " ", " ", " "],
            ["  BC102  ", "  TestClient2  ", "  Product Name  ", "  100.50  ", "  "],
            
            # Mixed empty/valid
            ["BC103", "TestClient3", "Valid Product", 50.0, ""],
            ["BC104", "TestClient1", "", 75.25, "https://example.com/image.jpg"],
            
            # Tab and newline characters
            ["BC105", "Test\tClient", "Product\nName", 100.0, ""],
        ]
        
        self.write_tsv(filename, headers, rows)

    def generate_duplicate_product_tsv(self, filename="duplicate_products.tsv"):
        """Generate product TSV with duplicate barcodes"""
        headers = ["barcode", "clientName", "name", "mrp", "imageUrl"]
        rows = [
            # Same barcode, different products
            ["DUPLICATE1", "TestClient1", "Product A", 100.0, ""],
            ["DUPLICATE1", "TestClient2", "Product B", 200.0, ""],
            ["DUPLICATE1", "TestClient3", "Product C", 300.0, ""],
            
            # Same product, different clients
            ["DUPLICATE2", "TestClient1", "Same Product", 150.0, ""],
            ["DUPLICATE2", "TestClient2", "Same Product", 150.0, ""],
            
            # Unique entries for comparison
            ["UNIQUE1", "TestClient1", "Unique Product 1", 50.0, ""],
            ["UNIQUE2", "TestClient2", "Unique Product 2", 75.0, ""],
        ]
        
        self.write_tsv(filename, headers, rows)

    def generate_column_misalignment_product_tsv(self, filename="misaligned_products.tsv"):
        """Generate product TSV with column misalignment issues"""
        headers = ["barcode", "clientName", "name", "mrp", "imageUrl"]
        
        # Create file with spaces instead of tabs (common issue)
        filepath = os.path.join(self.output_dir, filename)
        with open(filepath, 'w', newline='', encoding='utf-8') as f:
            # Write headers with tabs
            f.write('\t'.join(headers) + '\n')
            
            # Write some rows with spaces instead of tabs
            f.write('BC201 TestClient1 Product 1 100.0 https://example.com/image1.jpg\n')
            f.write('BC202 TestClient2 Product 2 200.0 https://example.com/image2.jpg\n')
            
            # Write some rows with extra columns
            f.write('BC203\tTestClient3\tProduct 3\t300.0\thttps://example.com/image3.jpg\textra_column\n')
            f.write('BC204\tTestClient1\tProduct 4\t400.0\thttps://example.com/image4.jpg\textra1\textra2\n')
            
            # Write some rows with missing columns
            f.write('BC205\tTestClient2\tProduct 5\n')
            f.write('BC206\tTestClient3\n')
        
        print(f"Generated: {filepath} (6 rows with column misalignment)")

    def generate_valid_inventory_tsv(self, filename="valid_inventory.tsv", count=100):
        """Generate valid inventory TSV"""
        headers = ["barcode", "quantity"]
        rows = []
        
        for i in range(count):
            row = [
                self.generate_random_barcode("INV", 8),
                random.randint(1, 1000)
            ]
            rows.append(row)
        
        self.write_tsv(filename, headers, rows)

    def generate_error_inventory_tsv(self, filename="error_inventory.tsv"):
        """Generate inventory TSV with various validation errors"""
        headers = ["barcode", "quantity"]
        rows = [
            # Missing required fields
            ["", 100],
            ["INV001", ""],
            ["", ""],
            
            # Invalid quantity values
            ["INV002", -10],
            ["INV003", 0],
            ["INV004", "not_a_number"],
            ["INV005", "abc"],
            ["INV006", 1.5],  # Decimal quantity
            
            # Very large quantities
            ["INV007", 2147483648],  # Integer overflow
            ["INV008", "999999999999999999999"],
            
            # Valid row for comparison
            ["INV009", 100]
        ]
        
        self.write_tsv(filename, headers, rows)

    def generate_comprehensive_test_suite(self):
        """Generate comprehensive test suite"""
        print("Generating comprehensive TSV test suite...")
        
        # Product TSVs
        self.generate_valid_product_tsv("valid_products_small.tsv", 10)
        self.generate_valid_product_tsv("valid_products_medium.tsv", 100)
        self.generate_valid_product_tsv("valid_products_large.tsv", 1000)
        self.generate_limit_test_product_tsv("limit_exceeded_products.tsv", 5001)
        self.generate_error_product_tsv("error_products.tsv")
        self.generate_null_empty_product_tsv("null_empty_products.tsv")
        self.generate_duplicate_product_tsv("duplicate_products.tsv")
        self.generate_column_misalignment_product_tsv("misaligned_products.tsv")
        
        # Inventory TSVs
        self.generate_valid_inventory_tsv("valid_inventory_small.tsv", 10)
        self.generate_valid_inventory_tsv("valid_inventory_medium.tsv", 100)
        self.generate_valid_inventory_tsv("valid_inventory_large.tsv", 1000)
        self.generate_error_inventory_tsv("error_inventory.tsv")
        
        # Edge cases
        self.generate_edge_case_tsvs()
        
        print(f"\nTest suite generated in '{self.output_dir}' directory")
        print("\nTest files created:")
        for file in sorted(os.listdir(self.output_dir)):
            if file.endswith('.tsv'):
                print(f"  - {file}")

    def generate_edge_case_tsvs(self):
        """Generate edge case TSVs"""
        
        # Empty file
        self.write_tsv("empty_products.tsv", ["barcode", "clientName", "name", "mrp", "imageUrl"], [])
        
        # Only headers
        self.write_tsv("headers_only_products.tsv", ["barcode", "clientName", "name", "mrp", "imageUrl"], [])
        
        # Single row
        self.write_tsv("single_row_products.tsv", ["barcode", "clientName", "name", "mrp", "imageUrl"], 
                      [["SINGLE1", "TestClient1", "Single Product", 99.99, ""]])
        
        # Unicode characters
        self.write_tsv("unicode_products.tsv", ["barcode", "clientName", "name", "mrp", "imageUrl"], [
            ["UNI001", "클라이언트", "제품명", 100.0, ""],
            ["UNI002", "Клиент", "Продукт", 200.0, ""],
            ["UNI003", "العميل", "المنتج", 300.0, ""],
            ["UNI004", "😀Client", "Product 😎", 400.0, ""]
        ])

    def create_test_summary(self):
        """Create a summary of all test files"""
        summary_file = os.path.join(self.output_dir, "TEST_SUMMARY.md")
        with open(summary_file, 'w', encoding='utf-8') as f:
            f.write("# TSV Test Files Summary\n\n")
            f.write(f"Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
            
            f.write("## Test Categories\n\n")
            f.write("### Valid Data Tests\n")
            f.write("- `valid_products_small.tsv` - 10 valid product records\n")
            f.write("- `valid_products_medium.tsv` - 100 valid product records\n")
            f.write("- `valid_products_large.tsv` - 1000 valid product records\n")
            f.write("- `valid_inventory_small.tsv` - 10 valid inventory records\n")
            f.write("- `valid_inventory_medium.tsv` - 100 valid inventory records\n")
            f.write("- `valid_inventory_large.tsv` - 1000 valid inventory records\n\n")
            
            f.write("### Limit Testing\n")
            f.write("- `limit_exceeded_products.tsv` - 5001 records (exceeds 5000 limit)\n\n")
            
            f.write("### Error Scenarios\n")
            f.write("- `error_products.tsv` - Various validation errors\n")
            f.write("- `error_inventory.tsv` - Various validation errors\n")
            f.write("- `null_empty_products.tsv` - Null and empty values\n")
            f.write("- `duplicate_products.tsv` - Duplicate barcode entries\n")
            f.write("- `misaligned_products.tsv` - Column misalignment issues\n\n")
            
            f.write("### Edge Cases\n")
            f.write("- `empty_products.tsv` - Empty file\n")
            f.write("- `headers_only_products.tsv` - Only headers, no data\n")
            f.write("- `single_row_products.tsv` - Single record\n")
            f.write("- `unicode_products.tsv` - Unicode characters\n\n")
            
            f.write("## Usage Instructions\n\n")
            f.write("1. **Valid Data Tests**: Should process successfully\n")
            f.write("2. **Limit Testing**: Should return appropriate error messages\n")
            f.write("3. **Error Scenarios**: Should generate error TSV files for download\n")
            f.write("4. **Edge Cases**: Test boundary conditions\n\n")
            
            f.write("## Expected Behaviors\n\n")
            f.write("- Valid files should upload successfully\n")
            f.write("- Error files should generate downloadable error reports\n")
            f.write("- Limit exceeded files should reject with appropriate message\n")
            f.write("- Column misalignment should be detected and reported\n")
            f.write("- Empty/null values should be handled gracefully\n")
            f.write("- Duplicate entries should be handled according to business rules\n")
        
        print(f"Created test summary: {summary_file}")

if __name__ == "__main__":
    generator = TSVTestGenerator()
    generator.generate_comprehensive_test_suite()
    generator.create_test_summary()
    print("\n✅ Comprehensive TSV test suite generated successfully!")
    print(f"📁 Check the '{generator.output_dir}' directory for all test files") 