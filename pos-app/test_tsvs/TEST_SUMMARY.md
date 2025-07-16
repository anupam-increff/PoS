# TSV Test Files Summary

Generated on: 2025-07-16 09:40:09

## Test Categories

### Valid Data Tests
- `valid_products_small.tsv` - 10 valid product records
- `valid_products_medium.tsv` - 100 valid product records
- `valid_products_large.tsv` - 1000 valid product records
- `valid_inventory_small.tsv` - 10 valid inventory records
- `valid_inventory_medium.tsv` - 100 valid inventory records
- `valid_inventory_large.tsv` - 1000 valid inventory records

### Limit Testing
- `limit_exceeded_products.tsv` - 5001 records (exceeds 5000 limit)

### Error Scenarios
- `error_products.tsv` - Various validation errors
- `error_inventory.tsv` - Various validation errors
- `null_empty_products.tsv` - Null and empty values
- `duplicate_products.tsv` - Duplicate barcode entries
- `misaligned_products.tsv` - Column misalignment issues

### Edge Cases
- `empty_products.tsv` - Empty file
- `headers_only_products.tsv` - Only headers, no data
- `single_row_products.tsv` - Single record
- `unicode_products.tsv` - Unicode characters

## Usage Instructions

1. **Valid Data Tests**: Should process successfully
2. **Limit Testing**: Should return appropriate error messages
3. **Error Scenarios**: Should generate error TSV files for download
4. **Edge Cases**: Test boundary conditions

## Expected Behaviors

- Valid files should upload successfully
- Error files should generate downloadable error reports
- Limit exceeded files should reject with appropriate message
- Column misalignment should be detected and reported
- Empty/null values should be handled gracefully
- Duplicate entries should be handled according to business rules
