ALTER TABLE scene ADD COLUMN paragraphs JSONB NOT NULL DEFAULT '[]'::JSONB;

UPDATE scene
SET paragraphs = COALESCE((
    SELECT jsonb_agg(
        CASE
            WHEN line ~ '^\s*## ' THEN jsonb_build_object('kind', 'SUBTITLE', 'level', 0, 'runs',
                jsonb_build_array(jsonb_build_object('text', regexp_replace(line, '^\s*##\s+', ''), 'bold', FALSE, 'italic', FALSE)))
            WHEN line ~ '^\s*# ' THEN jsonb_build_object('kind', 'TITLE', 'level', 0, 'runs',
                jsonb_build_array(jsonb_build_object('text', regexp_replace(line, '^\s*#\s+', ''), 'bold', FALSE, 'italic', FALSE)))
            WHEN line ~ '^\s*[-*] ' THEN jsonb_build_object('kind', 'BULLET', 'level', length(substring(line FROM '^ *')) / 2, 'runs',
                jsonb_build_array(jsonb_build_object('text', regexp_replace(line, '^\s*[-*]\s+', ''), 'bold', FALSE, 'italic', FALSE)))
            ELSE jsonb_build_object('kind', 'TEXT', 'level', 0, 'runs',
                jsonb_build_array(jsonb_build_object('text', btrim(line), 'bold', FALSE, 'italic', FALSE)))
        END
        ORDER BY position)
    FROM regexp_split_to_table(scene.description, E'\n') WITH ORDINALITY AS lines(line, position)
    WHERE btrim(line) <> ''
), '[]'::JSONB);

ALTER TABLE scene DROP COLUMN description;
ALTER TABLE scene RENAME COLUMN paragraphs TO description;
ALTER TABLE scene ALTER COLUMN description DROP DEFAULT;
