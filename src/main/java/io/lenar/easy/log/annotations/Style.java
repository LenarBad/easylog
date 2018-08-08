package io.lenar.easy.log.annotations;

/**
 * Styles that can be used for logging
 */
public enum  Style {

    /**
     * As pretty printed JSON, null fields are not serialized
     */
    PRETTY_PRINT_NO_NULLS(true, false),

    /**
     * As pretty printed JSON, null fields are serialized
     */
    PRETTY_PRINT_WITH_NULLS(true, true),

    /**
     * As JSON, null fields are serialized
     */
    COMPACT_WITH_NULLS(false, true),

    /**
     * As JSON, null fields are not serialized
     */
    MINIMAL(false, false),

    /**
     * Parameters and returned result will not be processed.
     * "toString()" method will be used for serialization
     * Since we do not process anything "maskFields" annotation parameter will be ignored
     */
    AS_IS(null, null);

    public Boolean prettyPrint;
    public Boolean logNulls;

    Style(Boolean prettyPrint, Boolean logNulls) {
        this.prettyPrint = prettyPrint;
        this.logNulls = logNulls;
    }

}
