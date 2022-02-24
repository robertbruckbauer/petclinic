import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';
export default class Radiobox extends SvelteTypedComponent<RadioboxProps, RadioboxEvents, RadioboxSlots> {
}

declare const _RadioboxProps: {

    /** 
     * Disabled state.
     */
    disabled?: boolean;

    /** 
     * Selected value.
     */
    value?: string[];

    /** 
     * Array of Items.
     */
    allItem?: ({value:string, text: string}|string)[];

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _RadioboxEvents: {
    change: Event;
};

declare const _RadioboxSlots: {
};

export declare type RadioboxProps = typeof _RadioboxProps;
export declare type RadioboxEvents = typeof _RadioboxEvents;
export declare type RadioboxSlots = typeof _RadioboxSlots;
export {
};
