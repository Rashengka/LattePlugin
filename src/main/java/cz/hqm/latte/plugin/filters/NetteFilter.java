package cz.hqm.latte.plugin.filters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a filter from a Nette package.
 * Contains information about the filter name, description, package, and parameters.
 */
public class NetteFilter {
    private final String name;
    private final String description;
    private final String packageName;
    private final List<FilterParameter> parameters;

    /**
     * Creates a new NetteFilter without parameters.
     *
     * @param name The name of the filter
     * @param description The description of the filter
     * @param packageName The name of the package that provides the filter
     */
    public NetteFilter(@NotNull String name, @NotNull String description, @NotNull String packageName) {
        this(name, description, packageName, null);
    }
    
    /**
     * Creates a new NetteFilter with parameters.
     *
     * @param name The name of the filter
     * @param description The description of the filter
     * @param packageName The name of the package that provides the filter
     * @param parameters The parameters of the filter
     */
    public NetteFilter(@NotNull String name, @NotNull String description, @NotNull String packageName, @Nullable List<FilterParameter> parameters) {
        this.name = name;
        this.description = description;
        this.packageName = packageName;
        this.parameters = parameters != null ? parameters : new ArrayList<>();
    }
    
    /**
     * Represents a parameter for a filter.
     */
    public static class FilterParameter {
        private final String name;
        private final String type;
        private final String description;
        private final boolean optional;
        
        /**
         * Creates a new FilterParameter.
         *
         * @param name The name of the parameter
         * @param type The type of the parameter
         * @param description The description of the parameter
         * @param optional Whether the parameter is optional
         */
        public FilterParameter(@NotNull String name, @NotNull String type, @NotNull String description, boolean optional) {
            this.name = name;
            this.type = type;
            this.description = description;
            this.optional = optional;
        }
        
        /**
         * Gets the name of the parameter.
         *
         * @return The parameter name
         */
        @NotNull
        public String getName() {
            return name;
        }
        
        /**
         * Gets the type of the parameter.
         *
         * @return The parameter type
         */
        @NotNull
        public String getType() {
            return type;
        }
        
        /**
         * Gets the description of the parameter.
         *
         * @return The parameter description
         */
        @NotNull
        public String getDescription() {
            return description;
        }
        
        /**
         * Checks if the parameter is optional.
         *
         * @return True if the parameter is optional, false otherwise
         */
        public boolean isOptional() {
            return optional;
        }
    }

    /**
     * Gets the name of the filter.
     *
     * @return The filter name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the filter.
     *
     * @return The filter description
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name of the package that provides the filter.
     *
     * @return The package name
     */
    @NotNull
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the parameters of the filter.
     *
     * @return The filter parameters
     */
    @NotNull
    public List<FilterParameter> getParameters() {
        return parameters;
    }
    
    /**
     * Checks if the filter has parameters.
     *
     * @return True if the filter has parameters, false otherwise
     */
    public boolean hasParameters() {
        return !parameters.isEmpty();
    }
    
    /**
     * Gets the display text for the filter in completion items.
     *
     * @return The display text
     */
    @NotNull
    public String getDisplayText() {
        if (hasParameters()) {
            StringBuilder sb = new StringBuilder(name);
            sb.append("(");
            
            for (int i = 0; i < parameters.size(); i++) {
                FilterParameter param = parameters.get(i);
                if (param.isOptional()) {
                    sb.append("[");
                }
                sb.append(param.getName());
                if (param.isOptional()) {
                    sb.append("]");
                }
                
                if (i < parameters.size() - 1) {
                    sb.append(", ");
                }
            }
            
            sb.append(") (").append(packageName).append(")");
            return sb.toString();
        }
        
        return name + " (" + packageName + ")";
    }

    /**
     * Gets the type text for the filter in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return packageName + " filter";
    }
    
    /**
     * Gets the parameter info text for the filter in completion items.
     *
     * @return The parameter info text or null if the filter has no parameters
     */
    @Nullable
    public String getParameterInfoText() {
        if (!hasParameters()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Parameters: ");
        
        for (int i = 0; i < parameters.size(); i++) {
            FilterParameter param = parameters.get(i);
            sb.append(param.getName()).append(" (").append(param.getType()).append(")");
            
            if (param.isOptional()) {
                sb.append(" [optional]");
            }
            
            if (!param.getDescription().isEmpty()) {
                sb.append(" - ").append(param.getDescription());
            }
            
            if (i < parameters.size() - 1) {
                sb.append(", ");
            }
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetteFilter that = (NetteFilter) o;

        if (!name.equals(that.name)) return false;
        return packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + packageName.hashCode();
        return result;
    }
}