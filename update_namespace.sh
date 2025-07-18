#!/bin/bash

# Function to update a single Java file
update_file() {
    local file=$1
    local new_file=$(echo $file | sed 's|org/latte|cz/hqm|')
    
    # Create the new file with updated package and imports
    sed 's|package org.latte|package cz.hqm.latte|g; s|import org.latte|import cz.hqm.latte|g' "$file" > "$new_file"
    
    echo "Updated: $file -> $new_file"
}

# Process main source files
echo "Processing main source files..."
find src/main/java/org/latte -name "*.java" | while read file; do
    update_file "$file"
done

echo "Main source files updated."

# Process test source files
echo "Processing test source files..."
find src/test/java/org/latte -name "*.java" | while read file; do
    update_file "$file"
done

echo "Test source files updated."