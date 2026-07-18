const fs = require('fs');

const filepath = process.argv[2];
const content = fs.readFileSync(filepath, 'utf-8');

// Format: [prop, label]
const todo = JSON.parse(process.argv[3]);

let result = content;
let changed = 0;

for (const [prop, label] of todo) {
  // Pattern: <el-table-column ... prop="X" ... >\n   <template slot-scope
  const regex = new RegExp(
    '(<el-table-column[^>]*prop="' + prop.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '"[^>]*>)\\s*\\n(\\s*)(<template slot-scope)'
  );

  const match = result.match(regex);
  if (match) {
    const indent = match[2];
    const headerBlock =
      indent + '<template slot="header">\n' +
      indent + '  <FilterHeader label="' + label + '" :value="columnSearch.' + prop + ' || []" :options="colFilterOptions.' + prop + ' || []" @update:value="columnSearch.' + prop + ' = $event" />\n' +
      indent + '</template>\n' +
      indent;

    result = result.replace(regex, '$1\n' + headerBlock + '$3');
    console.log('OK: ' + prop);
    changed++;
  } else {
    // Try without prop match (label-based)
    const regex2 = new RegExp(
      '(<el-table-column[^>]*label="' + label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '"(?:[^>]*prop="' + prop.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')?[^>]*>)\\s*\\n(\\s*)(<template slot-scope)'
    );
    const match2 = result.match(regex2);
    if (match2) {
      const indent = match2[2];
      const headerBlock =
        indent + '<template slot="header">\n' +
        indent + '  <FilterHeader label="' + label + '" :value="columnSearch.' + prop + ' || []" :options="colFilterOptions.' + prop + ' || []" @update:value="columnSearch.' + prop + ' = $event" />\n' +
        indent + '</template>\n' +
        indent;
      result = result.replace(regex2, '$1\n' + headerBlock + '$3');
      console.log('OK (alt): ' + prop);
      changed++;
    } else {
      // Try multiline el-table-column
      const regex3 = new RegExp(
        '(prop="' + prop.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '"[^>]*>)\\s*\\n(\\s*)(<template)'
      );
      const match3 = result.match(regex3);
      if (match3) {
        const indent = match3[2];
        const headerBlock =
          indent + '<template slot="header">\n' +
          indent + '  <FilterHeader label="' + label + '" :value="columnSearch.' + prop + ' || []" :options="colFilterOptions.' + prop + ' || []" @update:value="columnSearch.' + prop + ' = $event" />\n' +
          indent + '</template>\n' +
          indent;
        result = result.replace(regex3, '$1\n' + headerBlock + '$3');
        console.log('OK (ml): ' + prop);
        changed++;
      } else {
        console.log('FAIL: ' + prop + ' / ' + label);
      }
    }
  }
}

fs.writeFileSync(filepath, result, 'utf-8');
console.log('Done: ' + changed + ' columns');
