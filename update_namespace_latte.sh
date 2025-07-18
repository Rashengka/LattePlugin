#!/bin/bash

# Function to update a single Java file
update_file() {
    local file=$1
    local new_file=$(echo $file | sed 's|cz/hqm/plugin|cz/hqm/latte/plugin|')
    
    # Create the new file with updated package and imports
    sed 's|package cz.hqm.plugin|package cz.hqm.latte.plugin|g; s|import cz.hqm.plugin|import cz.hqm.latte.plugin|g' "$file" > "$new_file"
    
    echo "Updated: $file -> $new_file"
}

# Process main source files
echo "Processing main source files..."
find src/main/java/cz/hqm/plugin -name "*.java" | while read file; do
    update_file "$file"
done

echo "Main source files updated."

# Process test source files
echo "Processing test source files..."
find src/test/java/cz/hqm/plugin -name "*.java" | while read file; do
    update_file "$file"
done

echo "Test source files updated."