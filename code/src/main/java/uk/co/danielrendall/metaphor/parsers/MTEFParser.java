package uk.co.danielrendall.metaphor.parsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import uk.co.danielrendall.metaphor.ParseException;
import uk.co.danielrendall.metaphor.Parser;
import uk.co.danielrendall.metaphor.ParserRegistry;
import uk.co.danielrendall.metaphor.Record;
import uk.co.danielrendall.metaphor.records.MTEF;

import java.io.PushbackInputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Rendall
 */
public class MTEFParser extends Parser<MTEF> {

    private final static Map<Integer, String> platformMap;
    private final static Map<Integer, String> productMap;

    static {
        Map<Integer, String> _platformMap = Maps.newHashMap();
        _platformMap.put(0, "Macintosh");
        _platformMap.put(1, "Windows");
        platformMap = ImmutableMap.copyOf(_platformMap);

        Map<Integer, String> _productMap = Maps.newHashMap();
        _productMap.put(0, "MathType");
        _productMap.put(1, "Equation Editor");
        productMap = ImmutableMap.copyOf(_productMap);
    }

    @Override
    protected MTEF doParse(PushbackInputStream in) throws ParseException {
        String generatingPlatform = getFromMap(in, platformMap);
        String generatingProduct = getFromMap(in, productMap);
        int productVersion = readByte(in);
        int productSubversion = readByte(in);
        String applicationKey = readNullTerminatedString(in);
        int equationOptions = readByte(in);
        List<Record> records = Lists.newArrayList();
        int type;
        while ((type = nextType(in)) != -1) {
            Parser next = ParserRegistry.get(type);
            records.add(next.parse(in));
        }

        return new MTEF(generatingPlatform, generatingProduct, productVersion, productSubversion, applicationKey, equationOptions, records);
    }

    @Override
    protected int getInitialByte() {
        return ParserRegistry.MTEF;
    }
}