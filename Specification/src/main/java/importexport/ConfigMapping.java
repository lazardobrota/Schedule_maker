package importexport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigMapping implements Comparable<ConfigMapping>{

    private int index;
    private String custom;
    private String original;

    /**
     * Saves index, custom name and original name of config for file that needs to be imported or exported
     * @param index
     * @param custom
     * @param original
     */
    public ConfigMapping(int index, String custom, String original) {
        this.index = index;
        this.custom = custom;
        this.original = original;
    }

    /**
     * Sort configs with indexes, ascending sequence(/)
     * @param that
     * @return
     */
    @Override
    public int compareTo(ConfigMapping that) {
        return this.index - that.getIndex();
    }
}
